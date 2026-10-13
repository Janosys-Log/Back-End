package com.fleetops.CsvParaJson.exceptions;

import com.fleetops.CsvParaJson.dto.response.ErroDTO;
import com.fleetops.CsvParaJson.exceptions.tipos.ErroDeEncoding;
import com.fleetops.CsvParaJson.exceptions.tipos.ErrodeExtensao;
import com.fleetops.CsvParaJson.exceptions.tipos.NaoFoiPossivelAcharEncoding;
import com.fleetops.CsvParaJson.exceptions.tipos.NaoFoiPossivelLerArquivo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManipuladorExcessaoCsv {
    @ExceptionHandler(ErroDeEncoding.class)
    public ErroDTO manipularErroDeEncoding(ErroDeEncoding erro) {
        ErroDTO resposta = new ErroDTO(erro.getMessage(), erro.getMensagem());
        return resposta;
    }

    @ExceptionHandler(NaoFoiPossivelAcharEncoding.class)
    public ErroDTO manipularNaoAcharEncoding(NaoFoiPossivelAcharEncoding erro) {
        ErroDTO resposta = new ErroDTO(erro.getMessage(), erro.getMensagem());
        return resposta;
    }

    @ExceptionHandler(NaoFoiPossivelLerArquivo.class)
    public ErroDTO manipularNãolerArquivo(NaoFoiPossivelLerArquivo erro) {
        ErroDTO resposta = new ErroDTO(erro.getMessage(), erro.getMensagem());
        return resposta;
    }

    @ExceptionHandler(ErrodeExtensao.class)
    public ErroDTO manipularErrodeExtensao(ErrodeExtensao erro) {
        ErroDTO resposta = new ErroDTO(erro.getMessage(), erro.getMensagem());
        return resposta;
    }
}
