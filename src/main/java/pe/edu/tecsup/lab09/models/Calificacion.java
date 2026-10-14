package pe.edu.tecsup.lab09.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "calificaciones",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)          // Hibernate crea la FK 1
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)         // Hibernate crea la FK 2
    private Producto producto;

    @Column(nullable = false)
    private Integer puntuacion;                                  // 1 a 5

    @Column(length = 300)
    private String comentario;

    @Column(nullable = false)
    private LocalDate fecha;

    @PrePersist
    void prePersist() {
        if (fecha == null) {
            fecha = LocalDate.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
