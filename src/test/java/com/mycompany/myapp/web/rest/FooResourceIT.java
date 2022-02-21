package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Foo;
import com.mycompany.myapp.repository.FooRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link FooResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FooResourceIT {

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/foos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private FooRepository fooRepository;

    @Autowired
    private MockMvc restFooMockMvc;

    private Foo foo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createEntity() {
        Foo foo = new Foo().surname(DEFAULT_SURNAME).name(DEFAULT_NAME);
        return foo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Foo createUpdatedEntity() {
        Foo foo = new Foo().surname(UPDATED_SURNAME).name(UPDATED_NAME);
        return foo;
    }

    @BeforeEach
    public void initTest() {
        fooRepository.deleteAll();
        foo = createEntity();
    }

    @Test
    void createFoo() throws Exception {
        int databaseSizeBeforeCreate = fooRepository.findAll().size();
        // Create the Foo
        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isCreated());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeCreate + 1);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testFoo.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createFooWithExistingId() throws Exception {
        // Create the Foo with an existing ID
        foo.setId("existing_id");

        int databaseSizeBeforeCreate = fooRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFooMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFoos() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        // Get all the fooList
        restFooMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(foo.getId())))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    void getFoo() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        // Get the foo
        restFooMockMvc
            .perform(get(ENTITY_API_URL_ID, foo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(foo.getId()))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    void getNonExistingFoo() throws Exception {
        // Get the foo
        restFooMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putNewFoo() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo
        Foo updatedFoo = fooRepository.findById(foo.getId()).get();
        updatedFoo.surname(UPDATED_SURNAME).name(UPDATED_NAME);

        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFoo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testFoo.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, foo.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFooWithPatch() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo using partial update
        Foo partialUpdatedFoo = new Foo();
        partialUpdatedFoo.setId(foo.getId());

        partialUpdatedFoo.surname(UPDATED_SURNAME);

        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFoo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testFoo.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void fullUpdateFooWithPatch() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        int databaseSizeBeforeUpdate = fooRepository.findAll().size();

        // Update the foo using partial update
        Foo partialUpdatedFoo = new Foo();
        partialUpdatedFoo.setId(foo.getId());

        partialUpdatedFoo.surname(UPDATED_SURNAME).name(UPDATED_NAME);

        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFoo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFoo))
            )
            .andExpect(status().isOk());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
        Foo testFoo = fooList.get(fooList.size() - 1);
        assertThat(testFoo.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testFoo.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, foo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(foo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFoo() throws Exception {
        int databaseSizeBeforeUpdate = fooRepository.findAll().size();
        foo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFooMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(foo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Foo in the database
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFoo() throws Exception {
        // Initialize the database
        fooRepository.save(foo);

        int databaseSizeBeforeDelete = fooRepository.findAll().size();

        // Delete the foo
        restFooMockMvc.perform(delete(ENTITY_API_URL_ID, foo.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Foo> fooList = fooRepository.findAll();
        assertThat(fooList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
