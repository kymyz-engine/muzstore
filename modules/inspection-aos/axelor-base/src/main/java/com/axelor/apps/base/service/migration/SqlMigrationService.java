package com.axelor.apps.base.service.migration;

import com.axelor.apps.base.db.MigrationLog;
import com.axelor.apps.base.db.repo.MigrationLogRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SqlMigrationService {

    private static final Logger log = LoggerFactory.getLogger(SqlMigrationService.class);

    private final MigrationLogRepository migrationLogRepo;
    private final EntityManager em;

    @Inject
    public SqlMigrationService(MigrationLogRepository migrationLogRepo, EntityManager em) {
        this.migrationLogRepo = migrationLogRepo;
        this.em = em;
    }

    @Transactional(rollbackOn = Exception.class)
    public void runMigrations() {
        log.info("SQL Migrations: checking for new scripts in 'migrations/' folder...");

        Set<String> foundFiles = new TreeSet<>();

        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("migrations");

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                log.debug("Scanning resource URL: {}", url);

                if ("file".equals(url.getProtocol())) {
                    File dir = new File(url.toURI());
                    File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
                    if (files != null) {
                        for (File f : files) {
                            foundFiles.add("migrations/" + f.getName());
                        }
                    }
                } else if ("jar".equals(url.getProtocol())) {
                    String path = url.getPath();
                    String jarPath = path.substring(5, path.indexOf("!"));
                    try (java.util.jar.JarFile jar = new java.util.jar.JarFile(java.net.URLDecoder.decode(jarPath, "UTF-8"))) {
                        Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            String name = entries.nextElement().getName();
                            if (name.startsWith("migrations/") && name.endsWith(".sql") && name.length() > 11) {
                                foundFiles.add(name);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error scanning for migrations: " + e.getMessage(), e);
        }

        if (foundFiles.isEmpty()) {
            log.info("SQL Migrations: no .sql files found in classpath.");
            return;
        }

        log.info("Found {} migration file(s).", foundFiles.size());

        for (String resourcePath : foundFiles) {
            String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            String version = fileName.contains("__") ? fileName.split("__")[0] : fileName;

            if (isNew(version)) {
                apply(resourcePath, fileName, version);
            } else {
                log.debug("Migration {} already applied, skipping.", version);
            }
        }
    }

    private boolean isNew(String version) {
        return migrationLogRepo.all().filter("self.migrationVersion = :v").bind("v", version).fetchOne() == null;
    }

    private void apply(String resourcePath, String fileName, String version) {
        log.info("Applying SQL migration: {}", fileName);

        InputStream foundStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (foundStream == null) {
            foundStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        }

        if (foundStream == null) {
            log.error("Could not find resource file: {}", resourcePath);
            return;
        }

        try (InputStream is = foundStream) {
            String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            if (!sql.trim().isEmpty()) {
                org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
                session.doWork(connection -> connection.createStatement().execute(sql));
            }

            MigrationLog logEntry = new MigrationLog();
            logEntry.setMigrationVersion(version);
            logEntry.setDescription(fileName);
            logEntry.setAppliedAt(LocalDateTime.now());
            migrationLogRepo.save(logEntry);

            log.info("Successfully applied migration: {}", version);
        } catch (Exception e) {
            log.error("CRITICAL: Failed to apply SQL migration " + fileName, e);
            throw new RuntimeException("Migration failed, startup aborted", e);
        }
    }
}