package com.fleetops.CsvParaJson;

import com.fleetops.CsvParaJson.dto.response.ManifestoDTO;
import com.fleetops.CsvParaJson.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.CsvParaJson.dto.response.VeiculoDTO;
import com.fleetops.CsvParaJson.service.CriadorDeParse;
import com.fleetops.CsvParaJson.service.ValidarLinhasDoCsv;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EncodingAndParsingTest {

    private <T> List<T> parse(File file, Class<T> clazz, Charset charset) throws Exception {
        CriadorDeParse<T> criador = new CriadorDeParse<>();
        BeanListProcessor<T> processor = new BeanListProcessor<>(clazz);
        CsvParser parser = criador.getParser(processor);

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), charset)) {
            parser.parse(reader);
        }
        return processor.getBeans();
    }

    @Test
    void testMockManifestos() throws Exception {
        File f = new File("../../docs-testes/mo/manifestos_agosto.csv");
        List<ManifestoDTO> beans = parse(f, ManifestoDTO.class, StandardCharsets.UTF_8);
        System.out.println("Mock Manifestos beans: " + beans.size());
        ResultadoLinhasDoCsv<ManifestoDTO> res = new ValidarLinhasDoCsv<ManifestoDTO>().Validar(beans);
        System.out.println("Mock Manifestos valids: " + res.getValidos().size() + ", errors: "
                + res.getInvalidos().size());
        Assertions.assertTrue(res.getValidos().size() > 0);
        Assertions.assertTrue(res.getInvalidos().size() > 0);
    }

    @Test
    void testRealManifestosAbrJun() throws Exception {
        File f = new File("../../docs-testes/re/manifestos_abr-jun.csv");
        List<ManifestoDTO> beans = parse(f, ManifestoDTO.class, Charset.forName("WINDOWS-1252"));
        System.out.println("Real Manifestos AbrJun beans: " + beans.size());
        ResultadoLinhasDoCsv<ManifestoDTO> res = new ValidarLinhasDoCsv<ManifestoDTO>().Validar(beans);
        System.out.println("Real Manifestos AbrJun valids: " + res.getValidos().size() + ", errors: "
                + res.getInvalidos().size());
        if (!res.getInvalidos().isEmpty()) {
            System.out.println("First error: Line " + res.getInvalidos().get(0).getLinha() + " -> "
                    + res.getInvalidos().get(0).getMensagem());
        }
        Assertions.assertEquals(beans.size(), res.getValidos().size());
        Assertions.assertEquals(0, res.getInvalidos().size());
    }

    @Test
    void testRealManifestosJulAgo() throws Exception {
        File f = new File("../../docs-testes/re/manifestos_jul-ago.csv");
        List<ManifestoDTO> beans = parse(f, ManifestoDTO.class, Charset.forName("WINDOWS-1252"));
        System.out.println("Real Manifestos JulAgo beans: " + beans.size());
        ResultadoLinhasDoCsv<ManifestoDTO> res = new ValidarLinhasDoCsv<ManifestoDTO>().Validar(beans);
        System.out.println("Real Manifestos JulAgo valids: " + res.getValidos().size() + ", errors: "
                + res.getInvalidos().size());
        if (!res.getInvalidos().isEmpty()) {
            System.out.println("First error: Line " + res.getInvalidos().get(0).getLinha() + " -> "
                    + res.getInvalidos().get(0).getMensagem());
        }
        Assertions.assertEquals(beans.size(), res.getValidos().size());
        Assertions.assertEquals(0, res.getInvalidos().size());
    }

    @Test
    void testMockVeiculos() throws Exception {
        File f = new File("../../docs-testes/mo/veiculos_agosto.csv");
        List<VeiculoDTO> beans = parse(f, VeiculoDTO.class, StandardCharsets.UTF_8);
        System.out.println("Mock Veiculos beans: " + beans.size());
        ResultadoLinhasDoCsv<VeiculoDTO> res = new ValidarLinhasDoCsv<VeiculoDTO>().Validar(beans);
        System.out.println("Mock Veiculos valids: " + res.getValidos().size() + ", errors: "
                + res.getInvalidos().size());
        Assertions.assertTrue(res.getValidos().size() > 0);
        Assertions.assertTrue(res.getInvalidos().size() > 0);
    }

    @Test
    void testRealVeiculos() throws Exception {
        File f = new File("../../docs-testes/re/veiculos_04-09-2026_10-32.csv");
        List<VeiculoDTO> beans = parse(f, VeiculoDTO.class, Charset.forName("WINDOWS-1252"));
        System.out.println("Real Veiculos beans: " + beans.size());
        ResultadoLinhasDoCsv<VeiculoDTO> res = new ValidarLinhasDoCsv<VeiculoDTO>().Validar(beans);
        System.out.println("Real Veiculos valids: " + res.getValidos().size() + ", errors: "
                + res.getInvalidos().size());
        if (!res.getInvalidos().isEmpty()) {
            System.out.println("First error: Line " + res.getInvalidos().get(0).getLinha() + " -> "
                    + res.getInvalidos().get(0).getMensagem());
        }
        Assertions.assertEquals(beans.size(), res.getValidos().size());
        Assertions.assertEquals(0, res.getInvalidos().size());
    }
}
