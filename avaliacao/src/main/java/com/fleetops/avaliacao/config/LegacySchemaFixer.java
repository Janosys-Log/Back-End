package com.fleetops.avaliacao.config;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Bancos criados por versões antigas do serviço podem ter colunas extras NOT NULL em
 * avaliacoes_frete (ex.: num_manifesto/id_manifesto). O Hibernate (ddl-auto=update) nunca
 * remove nem relaxa colunas, então todo INSERT falhava com 500 — que o Spring Security
 * mostrava como 401. Aqui as colunas desconhecidas passam a aceitar NULL.
 */
@Component
public class LegacySchemaFixer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacySchemaFixer.class);
    private static final String TABELA = "avaliacoes_frete";
    private static final Set<String> COLUNAS_ATUAIS =
            Set.of("id", "nota", "feedback", "num_modal", "motorista_id", "data_registro");

    private final JdbcTemplate jdbc;

    public LegacySchemaFixer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<java.util.Map<String, Object>> colunas = jdbc.queryForList(
                    "SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS "
                            + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? "
                            + "AND IS_NULLABLE = 'NO' AND COLUMN_DEFAULT IS NULL AND EXTRA NOT LIKE '%auto_increment%'",
                    TABELA);
            for (var c : colunas) {
                String nome = String.valueOf(c.get("COLUMN_NAME"));
                if (COLUNAS_ATUAIS.contains(nome.toLowerCase())) {
                    continue;
                }
                String tipo = String.valueOf(c.get("COLUMN_TYPE"));
                jdbc.execute("ALTER TABLE `" + TABELA + "` MODIFY COLUMN `" + nome + "` " + tipo + " NULL");
                log.warn("Coluna legada {}.{} ajustada para aceitar NULL", TABELA, nome);
            }
        } catch (Exception e) {
            log.warn("Verificação de colunas legadas ignorada: {}", e.getMessage());
        }
    }
}
