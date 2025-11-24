package com.ru.facil.ru_facil.resources;

import com.ru.facil.ru_facil.enuns.MealType;
import com.ru.facil.ru_facil.entities.MealFeedback;
import com.ru.facil.ru_facil.feedback.dto.MealFeedbackRequest;
import com.ru.facil.ru_facil.feedback.dto.MealFeedbackResponse;
import com.ru.facil.ru_facil.repositories.MealFeedbackRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/feedback")
public class MealFeedbackResource {

    private final MealFeedbackRepository repo;

    public MealFeedbackResource(MealFeedbackRepository repo) {
        this.repo = repo;
    }

    @Operation(summary = "Envia avaliação de uma refeição (almoço/jantar)")
    @PostMapping("/refeicoes")
    public ResponseEntity<MealFeedbackResponse> criarFeedback(
            @Valid @RequestBody MealFeedbackRequest request) {

        MealFeedback entidade = new MealFeedback();
        entidade.setNome(request.nome());
        entidade.setEmail(request.email());
        entidade.setDate(request.date());
        entidade.setMealType(request.mealType());
        entidade.setRating(request.rating());
        entidade.setComentario(request.comentario());

        entidade = repo.save(entidade);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MealFeedbackResponse.of(entidade));
    }

    @Operation(summary = "Lista avaliações de refeições (por e-mail ou por data+refeição)")
    @GetMapping("/refeicoes")
    public List<MealFeedbackResponse> listarFeedback(
            @RequestParam(required = false) String email,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) MealType mealType) {

        List<MealFeedback> feedbacks;

        if (email != null && !email.isBlank()) {
            feedbacks = repo.findByEmailOrderByDateDesc(email);
        } else if (date != null && mealType != null) {
            feedbacks = repo.findByDateAndMealTypeOrderByCriadoEmDesc(date, mealType);
        } else {
            feedbacks = repo.findAll();
        }

        return feedbacks.stream()
                .map(MealFeedbackResponse::of)
                .toList();
    }

    @Operation(summary = "Busca uma avaliação específica por ID")
    @GetMapping("/refeicoes/{id}")
    public MealFeedbackResponse buscarPorId(@PathVariable Long id) {
        MealFeedback f = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Avaliação de refeição não encontrada"
                ));
        return MealFeedbackResponse.of(f);
    }
}
