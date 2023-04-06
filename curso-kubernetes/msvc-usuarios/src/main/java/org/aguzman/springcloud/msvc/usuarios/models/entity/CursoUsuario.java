package org.aguzman.springcloud.msvc.usuarios.models.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "cursos_usuarios")
public class CursoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", unique = true)
    private Long usuarioId;

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof CursoUsuario)){
            return false;
        }
        CursoUsuario objeto = (CursoUsuario) obj;
        return this.usuarioId != null && this.usuarioId.equals(objeto.usuarioId);
    }
}
