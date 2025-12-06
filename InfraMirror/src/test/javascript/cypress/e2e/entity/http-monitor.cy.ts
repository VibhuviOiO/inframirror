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

describe('HttpMonitor e2e test', () => {
  const httpMonitorPageUrl = '/http-monitor';
  const httpMonitorPageUrlPattern = new RegExp('/http-monitor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const httpMonitorSample = {
    name: 'ouch shush',
    method: 'experience',
    type: 'oof',
    intervalSeconds: 23792,
    timeoutSeconds: 26098,
    retryCount: 12436,
    retryDelaySeconds: 30405,
  };

  let httpMonitor;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/http-monitors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/http-monitors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/http-monitors/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (httpMonitor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/http-monitors/${httpMonitor.id}`,
      }).then(() => {
        httpMonitor = undefined;
      });
    }
  });

  it('HttpMonitors menu should load HttpMonitors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('http-monitor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HttpMonitor').should('exist');
    cy.url().should('match', httpMonitorPageUrlPattern);
  });

  describe('HttpMonitor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(httpMonitorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create HttpMonitor page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/http-monitor/new$'));
        cy.getEntityCreateUpdateHeading('HttpMonitor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpMonitorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/http-monitors',
          body: httpMonitorSample,
        }).then(({ body }) => {
          httpMonitor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/http-monitors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/http-monitors?page=0&size=20>; rel="last",<http://localhost/api/http-monitors?page=0&size=20>; rel="first"',
              },
              body: [httpMonitor],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(httpMonitorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details HttpMonitor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('httpMonitor');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpMonitorPageUrlPattern);
      });

      it('edit button click should load edit HttpMonitor page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HttpMonitor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpMonitorPageUrlPattern);
      });

      it('edit button click should load edit HttpMonitor page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HttpMonitor');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpMonitorPageUrlPattern);
      });

      it('last delete button click should delete instance of HttpMonitor', () => {
        cy.intercept('GET', '/api/http-monitors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('httpMonitor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpMonitorPageUrlPattern);

        httpMonitor = undefined;
      });
    });
  });

  describe('new HttpMonitor page', () => {
    beforeEach(() => {
      cy.visit(`${httpMonitorPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('HttpMonitor');
    });

    it('should create an instance of HttpMonitor', () => {
      cy.get(`[data-cy="name"]`).type('incidentally');
      cy.get(`[data-cy="name"]`).should('have.value', 'incidentally');

      cy.get(`[data-cy="method"]`).type('inside');
      cy.get(`[data-cy="method"]`).should('have.value', 'inside');

      cy.get(`[data-cy="type"]`).type('gosh inasm');
      cy.get(`[data-cy="type"]`).should('have.value', 'gosh inasm');

      cy.get(`[data-cy="url"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="url"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="headers"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="headers"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="body"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="body"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="intervalSeconds"]`).type('31473');
      cy.get(`[data-cy="intervalSeconds"]`).should('have.value', '31473');

      cy.get(`[data-cy="timeoutSeconds"]`).type('30286');
      cy.get(`[data-cy="timeoutSeconds"]`).should('have.value', '30286');

      cy.get(`[data-cy="retryCount"]`).type('18525');
      cy.get(`[data-cy="retryCount"]`).should('have.value', '18525');

      cy.get(`[data-cy="retryDelaySeconds"]`).type('31232');
      cy.get(`[data-cy="retryDelaySeconds"]`).should('have.value', '31232');

      cy.get(`[data-cy="responseTimeWarningMs"]`).type('13963');
      cy.get(`[data-cy="responseTimeWarningMs"]`).should('have.value', '13963');

      cy.get(`[data-cy="responseTimeCriticalMs"]`).type('8446');
      cy.get(`[data-cy="responseTimeCriticalMs"]`).should('have.value', '8446');

      cy.get(`[data-cy="uptimeWarningPercent"]`).type('533.86');
      cy.get(`[data-cy="uptimeWarningPercent"]`).should('have.value', '533.86');

      cy.get(`[data-cy="uptimeCriticalPercent"]`).type('18765.47');
      cy.get(`[data-cy="uptimeCriticalPercent"]`).should('have.value', '18765.47');

      cy.get(`[data-cy="includeResponseBody"]`).should('not.be.checked');
      cy.get(`[data-cy="includeResponseBody"]`).click();
      cy.get(`[data-cy="includeResponseBody"]`).should('be.checked');

      cy.get(`[data-cy="resendNotificationCount"]`).type('16977');
      cy.get(`[data-cy="resendNotificationCount"]`).should('have.value', '16977');

      cy.get(`[data-cy="certificateExpiryDays"]`).type('1553');
      cy.get(`[data-cy="certificateExpiryDays"]`).should('have.value', '1553');

      cy.get(`[data-cy="ignoreTlsError"]`).should('not.be.checked');
      cy.get(`[data-cy="ignoreTlsError"]`).click();
      cy.get(`[data-cy="ignoreTlsError"]`).should('be.checked');

      cy.get(`[data-cy="checkSslCertificate"]`).should('not.be.checked');
      cy.get(`[data-cy="checkSslCertificate"]`).click();
      cy.get(`[data-cy="checkSslCertificate"]`).should('be.checked');

      cy.get(`[data-cy="checkDnsResolution"]`).should('not.be.checked');
      cy.get(`[data-cy="checkDnsResolution"]`).click();
      cy.get(`[data-cy="checkDnsResolution"]`).should('be.checked');

      cy.get(`[data-cy="upsideDownMode"]`).should('not.be.checked');
      cy.get(`[data-cy="upsideDownMode"]`).click();
      cy.get(`[data-cy="upsideDownMode"]`).should('be.checked');

      cy.get(`[data-cy="maxRedirects"]`).type('21547');
      cy.get(`[data-cy="maxRedirects"]`).should('have.value', '21547');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="tags"]`).type('regarding wasabi versus');
      cy.get(`[data-cy="tags"]`).should('have.value', 'regarding wasabi versus');

      cy.get(`[data-cy="enabled"]`).should('not.be.checked');
      cy.get(`[data-cy="enabled"]`).click();
      cy.get(`[data-cy="enabled"]`).should('be.checked');

      cy.get(`[data-cy="expectedStatusCodes"]`).type('weight whereas remand');
      cy.get(`[data-cy="expectedStatusCodes"]`).should('have.value', 'weight whereas remand');

      cy.get(`[data-cy="performanceBudgetMs"]`).type('27981');
      cy.get(`[data-cy="performanceBudgetMs"]`).should('have.value', '27981');

      cy.get(`[data-cy="sizeBudgetKb"]`).type('7551');
      cy.get(`[data-cy="sizeBudgetKb"]`).should('have.value', '7551');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        httpMonitor = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', httpMonitorPageUrlPattern);
    });
  });
});
