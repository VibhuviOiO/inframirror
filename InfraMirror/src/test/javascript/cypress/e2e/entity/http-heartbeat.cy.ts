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

describe('HttpHeartbeat e2e test', () => {
  const httpHeartbeatPageUrl = '/http-heartbeat';
  const httpHeartbeatPageUrlPattern = new RegExp('/http-heartbeat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const httpHeartbeatSample = { executedAt: '2025-12-05T00:03:31.013Z' };

  let httpHeartbeat;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/http-heartbeats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/http-heartbeats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/http-heartbeats/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (httpHeartbeat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/http-heartbeats/${httpHeartbeat.id}`,
      }).then(() => {
        httpHeartbeat = undefined;
      });
    }
  });

  it('HttpHeartbeats menu should load HttpHeartbeats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('http-heartbeat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('HttpHeartbeat').should('exist');
    cy.url().should('match', httpHeartbeatPageUrlPattern);
  });

  describe('HttpHeartbeat page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(httpHeartbeatPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create HttpHeartbeat page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/http-heartbeat/new$'));
        cy.getEntityCreateUpdateHeading('HttpHeartbeat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpHeartbeatPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/http-heartbeats',
          body: httpHeartbeatSample,
        }).then(({ body }) => {
          httpHeartbeat = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/http-heartbeats+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/http-heartbeats?page=0&size=20>; rel="last",<http://localhost/api/http-heartbeats?page=0&size=20>; rel="first"',
              },
              body: [httpHeartbeat],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(httpHeartbeatPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details HttpHeartbeat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('httpHeartbeat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpHeartbeatPageUrlPattern);
      });

      it('edit button click should load edit HttpHeartbeat page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HttpHeartbeat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpHeartbeatPageUrlPattern);
      });

      it('edit button click should load edit HttpHeartbeat page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('HttpHeartbeat');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpHeartbeatPageUrlPattern);
      });

      it('last delete button click should delete instance of HttpHeartbeat', () => {
        cy.intercept('GET', '/api/http-heartbeats/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('httpHeartbeat').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', httpHeartbeatPageUrlPattern);

        httpHeartbeat = undefined;
      });
    });
  });

  describe('new HttpHeartbeat page', () => {
    beforeEach(() => {
      cy.visit(`${httpHeartbeatPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('HttpHeartbeat');
    });

    it('should create an instance of HttpHeartbeat', () => {
      cy.get(`[data-cy="executedAt"]`).type('2025-12-05T02:24');
      cy.get(`[data-cy="executedAt"]`).blur();
      cy.get(`[data-cy="executedAt"]`).should('have.value', '2025-12-05T02:24');

      cy.get(`[data-cy="success"]`).should('not.be.checked');
      cy.get(`[data-cy="success"]`).click();
      cy.get(`[data-cy="success"]`).should('be.checked');

      cy.get(`[data-cy="responseTimeMs"]`).type('6731');
      cy.get(`[data-cy="responseTimeMs"]`).should('have.value', '6731');

      cy.get(`[data-cy="responseSizeBytes"]`).type('16723');
      cy.get(`[data-cy="responseSizeBytes"]`).should('have.value', '16723');

      cy.get(`[data-cy="responseStatusCode"]`).type('5544');
      cy.get(`[data-cy="responseStatusCode"]`).should('have.value', '5544');

      cy.get(`[data-cy="responseContentType"]`).type('who whereas entire');
      cy.get(`[data-cy="responseContentType"]`).should('have.value', 'who whereas entire');

      cy.get(`[data-cy="responseServer"]`).type('orderly provided');
      cy.get(`[data-cy="responseServer"]`).should('have.value', 'orderly provided');

      cy.get(`[data-cy="responseCacheStatus"]`).type('beyond');
      cy.get(`[data-cy="responseCacheStatus"]`).should('have.value', 'beyond');

      cy.get(`[data-cy="dnsLookupMs"]`).type('30383');
      cy.get(`[data-cy="dnsLookupMs"]`).should('have.value', '30383');

      cy.get(`[data-cy="dnsResolvedIp"]`).type('equal');
      cy.get(`[data-cy="dnsResolvedIp"]`).should('have.value', 'equal');

      cy.get(`[data-cy="tcpConnectMs"]`).type('3780');
      cy.get(`[data-cy="tcpConnectMs"]`).should('have.value', '3780');

      cy.get(`[data-cy="tlsHandshakeMs"]`).type('26365');
      cy.get(`[data-cy="tlsHandshakeMs"]`).should('have.value', '26365');

      cy.get(`[data-cy="sslCertificateValid"]`).should('not.be.checked');
      cy.get(`[data-cy="sslCertificateValid"]`).click();
      cy.get(`[data-cy="sslCertificateValid"]`).should('be.checked');

      cy.get(`[data-cy="sslCertificateExpiry"]`).type('2025-12-04T18:09');
      cy.get(`[data-cy="sslCertificateExpiry"]`).blur();
      cy.get(`[data-cy="sslCertificateExpiry"]`).should('have.value', '2025-12-04T18:09');

      cy.get(`[data-cy="sslCertificateIssuer"]`).type('final');
      cy.get(`[data-cy="sslCertificateIssuer"]`).should('have.value', 'final');

      cy.get(`[data-cy="sslDaysUntilExpiry"]`).type('24347');
      cy.get(`[data-cy="sslDaysUntilExpiry"]`).should('have.value', '24347');

      cy.get(`[data-cy="timeToFirstByteMs"]`).type('16013');
      cy.get(`[data-cy="timeToFirstByteMs"]`).should('have.value', '16013');

      cy.get(`[data-cy="warningThresholdMs"]`).type('9968');
      cy.get(`[data-cy="warningThresholdMs"]`).should('have.value', '9968');

      cy.get(`[data-cy="criticalThresholdMs"]`).type('24209');
      cy.get(`[data-cy="criticalThresholdMs"]`).should('have.value', '24209');

      cy.get(`[data-cy="errorType"]`).type('silky');
      cy.get(`[data-cy="errorType"]`).should('have.value', 'silky');

      cy.get(`[data-cy="errorMessage"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="errorMessage"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="rawRequestHeaders"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="rawRequestHeaders"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="rawResponseHeaders"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="rawResponseHeaders"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="rawResponseBody"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="rawResponseBody"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="dnsDetails"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="dnsDetails"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="tlsDetails"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="tlsDetails"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="httpVersion"]`).type('indeed for');
      cy.get(`[data-cy="httpVersion"]`).should('have.value', 'indeed for');

      cy.get(`[data-cy="contentEncoding"]`).type('jury');
      cy.get(`[data-cy="contentEncoding"]`).should('have.value', 'jury');

      cy.get(`[data-cy="compressionRatio"]`).type('11844.64');
      cy.get(`[data-cy="compressionRatio"]`).should('have.value', '11844.64');

      cy.get(`[data-cy="transferEncoding"]`).type('calmly gee vibration');
      cy.get(`[data-cy="transferEncoding"]`).should('have.value', 'calmly gee vibration');

      cy.get(`[data-cy="responseBodyHash"]`).type('junior yippee monumental');
      cy.get(`[data-cy="responseBodyHash"]`).should('have.value', 'junior yippee monumental');

      cy.get(`[data-cy="responseBodySample"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="responseBodySample"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="responseBodyValid"]`).should('not.be.checked');
      cy.get(`[data-cy="responseBodyValid"]`).click();
      cy.get(`[data-cy="responseBodyValid"]`).should('be.checked');

      cy.get(`[data-cy="responseBodyUncompressedBytes"]`).type('22481');
      cy.get(`[data-cy="responseBodyUncompressedBytes"]`).should('have.value', '22481');

      cy.get(`[data-cy="redirectDetails"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="redirectDetails"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="cacheControl"]`).type('nor');
      cy.get(`[data-cy="cacheControl"]`).should('have.value', 'nor');

      cy.get(`[data-cy="etag"]`).type('react gracefully');
      cy.get(`[data-cy="etag"]`).should('have.value', 'react gracefully');

      cy.get(`[data-cy="cacheAge"]`).type('24037');
      cy.get(`[data-cy="cacheAge"]`).should('have.value', '24037');

      cy.get(`[data-cy="cdnProvider"]`).type('except furthermore');
      cy.get(`[data-cy="cdnProvider"]`).should('have.value', 'except furthermore');

      cy.get(`[data-cy="cdnPop"]`).type('wherever');
      cy.get(`[data-cy="cdnPop"]`).should('have.value', 'wherever');

      cy.get(`[data-cy="rateLimitDetails"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="rateLimitDetails"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="networkPath"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="networkPath"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="agentMetrics"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="agentMetrics"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="phaseLatencies"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="phaseLatencies"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        httpHeartbeat = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', httpHeartbeatPageUrlPattern);
    });
  });
});
