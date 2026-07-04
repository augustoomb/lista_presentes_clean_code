package com.cleanarch.wishlist.domain.vo;

public record ProductId(String value) {

    public ProductId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProdutId não pode ser nulo ou vazio");
        }
    }

    @Override
    public boolean equals(Object o) {
       if (this == o) return true;
       if (!(o instanceof ProductId(String value1))) return false;
       return value.equals(value1);
    }

    @Override
    public String toString() {
        return value;
    }
}
