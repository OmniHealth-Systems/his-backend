package com.clinic.pacientes_service.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aseguradora")
public class Aseguradora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Nombre de la aseguradora

    @Column(nullable = true)
    private String telefono; // Teléfono de contacto de la aseguradora (opcional)

    @Column(nullable = true)
    private String email; // Email de contacto de la aseguradora (opcional)
}