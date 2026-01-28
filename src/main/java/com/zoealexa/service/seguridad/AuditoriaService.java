package com.zoealexa.service.seguridad;

import com.zoealexa.entity.auditoria.AuditoriaAcceso;
import com.zoealexa.entity.enums.Accion;
import com.zoealexa.entity.enums.Resultado;
import com.zoealexa.entity.seguridad.Usuario;
import com.zoealexa.repository.auditoria.AuditoriaAccesoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService {

    private final AuditoriaAccesoRepository auditoriaAccesoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resgisterLoginWithoutUser(String email, String ip, String userAgent, String detalle){

        AuditoriaAcceso auditoria = AuditoriaAcceso.builder()
                .usuario(null)
                .emailIntento(email)
                .accion(Accion.LOGIN_FALLIDO)
                .ip(ip)
                .userAgent(userAgent)
                .motivo(detalle)
                .resultado(Resultado.FALLIDO)
                .fechaHora(LocalDateTime.now())
                .build();

        auditoriaAccesoRepository.save(auditoria);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resgisterLoginWithUser(Usuario usuario, String email, Accion accion,String ip, String userAgent, String detalle, Resultado resultado){
        AuditoriaAcceso auditoria = AuditoriaAcceso.builder()
                .usuario(usuario)
                .emailIntento(email)
                .accion(accion)
                .ip(ip)
                .userAgent(userAgent)
                .motivo(detalle)
                .resultado(resultado)
                .fechaHora(LocalDateTime.now())
                .build();

        auditoriaAccesoRepository.save(auditoria);
    }
}
