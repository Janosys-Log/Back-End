package com.fleetops.CsvParaJson.service;

import com.fleetops.CsvParaJson.config.RabbitConfig;
import com.fleetops.CsvParaJson.dto.response.ResultadoLinhasDoCsv;
import com.fleetops.CsvParaJson.exceptions.tipos.ArquivoInexistente;
import com.fleetops.CsvParaJson.exceptions.tipos.ErroDeServicodeConsumo;
import com.fleetops.CsvParaJson.exceptions.tipos.ErrodeExtensao;
import com.fleetops.CsvParaJson.exceptions.tipos.NaoFoiPossivelAcharEncoding;
import com.fleetops.CsvParaJson.exceptions.tipos.NaoFoiPossivelLerArquivo;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CsvParaJson<T> {
    @Autowired
    private CriadorDeParse<T> criadorDeParser;

    @Autowired
    private DetectorDeEncoding detectoEncoding;

    @Autowired
    private ValidarLinhasDoCsv<T> validador;

    @Autowired
    private VerificadorDeExtensao verificadorDeExtensao;

    private final RabbitTemplate rabbitmq;

    public ResultadoLinhasDoCsv<T> RetornarJson(MultipartFile arquivo, Class<T> tipo, String filaParaEnviar) {
        if (arquivo == null || tipo == null || filaParaEnviar == null) {
            throw new ArquivoInexistente("Verifique sua requisição", "A requisição possui um arquivo faltando");
        }
        List<T> objetos = this.LerCsv(arquivo, tipo);

        ResultadoLinhasDoCsv<T> resultado = validador.Validar(objetos);

        Boolean funcionou = rabbitmq.convertSendAndReceiveAsType(
                RabbitConfig.Exchange, filaParaEnviar, resultado, new ParameterizedTypeReference<Boolean>() {});
        if (funcionou == null) {
            throw new ErroDeServicodeConsumo("Problema ao salvar os dados do csv", "Servico Não Respondeu");
        }

        return resultado;
    }

    private List<T> LerCsv(MultipartFile arquivo, Class<T> tipo) {
        BeanListProcessor<T> processador = new BeanListProcessor<>(tipo);
        CsvParser parser = criadorDeParser.getParser(processador);

        if (!verificadorDeExtensao.ECsv(arquivo.getOriginalFilename())) {
            throw new ErrodeExtensao("Esse arquivo não possui a extensão correta", "Só permitimos arquivos .csv");
        }

        /*
         * try {
         * if (!detectoEncoding.detectarEncoding(arquivo.getBytes())) {
         * throw new ErroDeEncoding(
         * "O arquivo " + arquivo.getOriginalFilename() + " não possui encoding UTF-8",
         * "Passe esse arquivo para o o Encoding UTF-8");
         * }
         *
         * } catch (IOException e) {
         * throw new NaoFoiPossivelAcharEncoding(
         * "A leitura desse arquivo falhou", "Não foi possivel encontrar o encoding");
         * }
         */
        String encoding;
        try {
            encoding = detectoEncoding.detectarEncoding(arquivo.getBytes());
        } catch (IOException e) {
            throw new NaoFoiPossivelAcharEncoding(
                    "A leitura desse arquivo falhou", "Não foi possivel encontrar o encoding");
        }
        ;
        System.out.println(encoding);
        try (Reader reader = new InputStreamReader(arquivo.getInputStream(), Charset.forName(encoding))) {
            parser.parse(reader);
        } catch (IOException e) {
            throw new NaoFoiPossivelLerArquivo(
                    "O arquivo " + arquivo.getOriginalFilename() + " não foi lido", "Problema ao ler esse arquivo");
        }

        List<T> objetos = processador.getBeans();

        return objetos;
    }
}
