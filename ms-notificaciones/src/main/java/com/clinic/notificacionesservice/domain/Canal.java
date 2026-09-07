package com.clinic.notificacionesservice.domain;

import com.clinic.notificacionesservice.domain.enums.TipoCanal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "canales")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Canal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoCanal tipo;

    private boolean habilitado;
}