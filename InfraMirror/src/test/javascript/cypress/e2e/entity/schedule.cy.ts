import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Schedule e2e test', () => {
  const schedulePageUrl = '/schedule';
  const schedulePageUrlPattern = new RegExp('/schedule(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const scheduleSample = { name: 'chunder devise', interval: 3086 };

  let schedule;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/schedules+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/schedules').as('postEntityRequest');
    cy.intercept('DELETE', '/api/schedules/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (schedule) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/schedules/${schedule.id}`,
      }).then(() => {
        schedule = undefined;
      });
    }
  });

  it('Schedules menu should load Schedules page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('schedule');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Schedule').should('exist');
    cy.url().should('match', schedulePageUrlPattern);
  });

  describe('Schedule page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(schedulePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Schedule page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/schedule/new$'));
        cy.getEntityCreateUpdateHeading('Schedule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schedulePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/schedules',
          body: scheduleSample,
        }).then(({ body }) => {
          schedule = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/schedules+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [schedule],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(schedulePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Schedule page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('schedule');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schedulePageUrlPattern);
      });

      it('edit button click should load edit Schedule page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Schedule');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schedulePageUrlPattern);
      });

      it('edit button click should load edit Schedule page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Schedule');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schedulePageUrlPattern);
      });

      it('last delete button click should delete instance of Schedule', () => {
        cy.intercept('GET', '/api/schedules/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('schedule').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', schedulePageUrlPattern);

        schedule = undefined;
      });
    });
  });

  describe('new Schedule page', () => {
    beforeEach(() => {
      cy.visit(`${schedulePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Schedule');
    });

    it('should create an instance of Schedule', () => {
      cy.get(`[data-cy="name"]`).type('wasabi recklessly yippee');
      cy.get(`[data-cy="name"]`).should('have.value', 'wasabi recklessly yippee');

      cy.get(`[data-cy="interval"]`).type('19619');
      cy.get(`[data-cy="interval"]`).should('have.value', '19619');

      cy.get(`[data-cy="includeResponseBody"]`).should('not.be.checked');
      cy.get(`[data-cy="includeResponseBody"]`).click();
      cy.get(`[data-cy="includeResponseBody"]`).should('be.checked');

      cy.get(`[data-cy="thresholdsWarning"]`).type('2379');
      cy.get(`[data-cy="thresholdsWarning"]`).should('have.value', '2379');

      cy.get(`[data-cy="thresholdsCritical"]`).type('12561');
      cy.get(`[data-cy="thresholdsCritical"]`).should('have.value', '12561');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        schedule = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', schedulePageUrlPattern);
    });
  });
});
