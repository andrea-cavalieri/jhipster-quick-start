package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Foo.
 */
@Document(collection = "foo")
public class Foo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("surname")
    private String surname;

    @Field("name")
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Foo id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurname() {
        return this.surname;
    }

    public Foo surname(String surname) {
        this.setSurname(surname);
        return this;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return this.name;
    }

    public Foo name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Foo)) {
            return false;
        }
        return id != null && id.equals(((Foo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Foo{" +
            "id=" + getId() +
            ", surname='" + getSurname() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
