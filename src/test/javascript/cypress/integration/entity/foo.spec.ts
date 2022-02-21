import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Foo e2e test', () => {
  const fooPageUrl = '/foo';
  const fooPageUrlPattern = new RegExp('/foo(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const fooSample = {};

  let foo: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/foos+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/foos').as('postEntityRequest');
    cy.intercept('DELETE', '/api/foos/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (foo) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/foos/${foo.id}`,
      }).then(() => {
        foo = undefined;
      });
    }
  });

  it('Foos menu should load Foos page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('foo');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Foo').should('exist');
    cy.url().should('match', fooPageUrlPattern);
  });

  describe('Foo page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(fooPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Foo page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/foo/new$'));
        cy.getEntityCreateUpdateHeading('Foo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', fooPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/foos',
          body: fooSample,
        }).then(({ body }) => {
          foo = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/foos+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/foos?page=0&size=20>; rel="last",<http://localhost/api/foos?page=0&size=20>; rel="first"',
              },
              body: [foo],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(fooPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Foo page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('foo');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', fooPageUrlPattern);
      });

      it('edit button click should load edit Foo page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Foo');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', fooPageUrlPattern);
      });

      it('last delete button click should delete instance of Foo', () => {
        cy.intercept('GET', '/api/foos/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('foo').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', fooPageUrlPattern);

        foo = undefined;
      });
    });
  });

  describe('new Foo page', () => {
    beforeEach(() => {
      cy.visit(`${fooPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Foo');
    });

    it('should create an instance of Foo', () => {
      cy.get(`[data-cy="surname"]`).type('Cambridgeshire bypass').should('have.value', 'Cambridgeshire bypass');

      cy.get(`[data-cy="name"]`).type('Granite program').should('have.value', 'Granite program');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        foo = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', fooPageUrlPattern);
    });
  });
});
