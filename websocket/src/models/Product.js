class Product {
    constructor(data) {
        this.id = data.id;
        this.id_vendedor = data.id_vendedor;
        this.nombre = data.nombre;
        this.descripcion = data.descripcion;
        this.precio = data.precio;
        this.stock = data.stock;
        this.categoria = data.categoria;
        this.imagen_url = data.imagen_url;
        this.disponible = data.disponible;
        this.nombre_vendedor = data.nombre_vendedor;
        this.telefono_vendedor = data.telefono_vendedor;
        this.created_at = data.created_at;
    }

    toJSON() {
        return {
            id: this.id,
            id_vendedor: this.id_vendedor,
            nombre: this.nombre,
            descripcion: this.descripcion,
            precio: this.precio,
            stock: this.stock,
            categoria: this.categoria,
            imagen_url: this.imagen_url,
            disponible: this.disponible,
            nombre_vendedor: this.nombre_vendedor,
            telefono_vendedor: this.telefono_vendedor,
            created_at: this.created_at
        };
    }
}

module.exports = Product;
