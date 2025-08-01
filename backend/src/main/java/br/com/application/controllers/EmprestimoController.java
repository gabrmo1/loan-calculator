package br.com.application.controllers;

import br.com.application.models.dtos.CalcularEmprestimoRequest;
import br.com.application.models.dtos.DetalhesEmprestimo;
import br.com.application.services.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/emprestimos")
@Tag(name = "Empréstimo", description = "Operações relacionadas a empréstimos")
public class EmprestimoController {

    private final EmprestimoService service;

    @PostMapping(value = "/calcular-prestacoes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calcular Prestações", description = "Retorna um cálculo das prestações do empréstimo, de acordo com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = DetalhesEmprestimo.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<DetalhesEmprestimo>> calcularJurosEmprestimo(@RequestBody @Valid final CalcularEmprestimoRequest request) {
        return ResponseEntity.ok(service.detalharEmprestimo(request));
    }

}
