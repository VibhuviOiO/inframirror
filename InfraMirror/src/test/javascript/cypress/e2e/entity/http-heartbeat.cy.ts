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
  const httpHeartbeatSample = { executedAt: '2025-12-04T12:00:17.580Z' };

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
      cy.get(`[data-cy="executedAt"]`).type('2025-12-04T11:36');
      cy.get(`[data-cy="executedAt"]`).blur();
      cy.get(`[data-cy="executedAt"]`).should('have.value', '2025-12-04T11:36');

      cy.get(`[data-cy="success"]`).should('not.be.checked');
      cy.get(`[data-cy="success"]`).click();
      cy.get(`[data-cy="success"]`).should('be.checked');

      cy.get(`[data-cy="responseTimeMs"]`).type('21334');
      cy.get(`[data-cy="responseTimeMs"]`).should('have.value', '21334');

      cy.get(`[data-cy="responseSizeBytes"]`).type('20405');
      cy.get(`[data-cy="responseSizeBytes"]`).should('have.value', '20405');

      cy.get(`[data-cy="responseStatusCode"]`).type('26682');
      cy.get(`[data-cy="responseStatusCode"]`).should('have.value', '26682');

      cy.get(`[data-cy="responseContentType"]`).type('majestically coin');
      cy.get(`[data-cy="responseContentType"]`).should('have.value', 'majestically coin');

      cy.get(`[data-cy="responseServer"]`).type('oh approximate self-assured');
      cy.get(`[data-cy="responseServer"]`).should('have.value', 'oh approximate self-assured');

      cy.get(`[data-cy="responseCacheStatus"]`).type('grok ham whoever');
      cy.get(`[data-cy="responseCacheStatus"]`).should('have.value', 'grok ham whoever');

      cy.get(`[data-cy="dnsLookupMs"]`).type('25577');
      cy.get(`[data-cy="dnsLookupMs"]`).should('have.value', '25577');

      cy.get(`[data-cy="dnsResolvedIp"]`).type('to astride around');
      cy.get(`[data-cy="dnsResolvedIp"]`).should('have.value', 'to astride around');

      cy.get(`[data-cy="tcpConnectMs"]`).type('13444');
      cy.get(`[data-cy="tcpConnectMs"]`).should('have.value', '13444');

      cy.get(`[data-cy="tlsHandshakeMs"]`).type('3884');
      cy.get(`[data-cy="tlsHandshakeMs"]`).should('have.value', '3884');

      cy.get(`[data-cy="sslCertificateValid"]`).should('not.be.checked');
      cy.get(`[data-cy="sslCertificateValid"]`).click();
      cy.get(`[data-cy="sslCertificateValid"]`).should('be.checked');

      cy.get(`[data-cy="sslCertificateExpiry"]`).type('2025-12-04T06:19');
      cy.get(`[data-cy="sslCertificateExpiry"]`).blur();
      cy.get(`[data-cy="sslCertificateExpiry"]`).should('have.value', '2025-12-04T06:19');

      cy.get(`[data-cy="sslCertificateIssuer"]`).type('immediately');
      cy.get(`[data-cy="sslCertificateIssuer"]`).should('have.value', 'immediately');

      cy.get(`[data-cy="sslDaysUntilExpiry"]`).type('31820');
      cy.get(`[data-cy="sslDaysUntilExpiry"]`).should('have.value', '31820');

      cy.get(`[data-cy="timeToFirstByteMs"]`).type('23273');
      cy.get(`[data-cy="timeToFirstByteMs"]`).should('have.value', '23273');

      cy.get(`[data-cy="warningThresholdMs"]`).type('20152');
      cy.get(`[data-cy="warningThresholdMs"]`).should('have.value', '20152');

      cy.get(`[data-cy="criticalThresholdMs"]`).type('8860');
      cy.get(`[data-cy="criticalThresholdMs"]`).should('have.value', '8860');

      cy.get(`[data-cy="errorType"]`).type('whoa pacemaker magnetize');
      cy.get(`[data-cy="errorType"]`).should('have.value', 'whoa pacemaker magnetize');

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

      cy.get(`[data-cy="httpVersion"]`).type('selfishly ');
      cy.get(`[data-cy="httpVersion"]`).should('have.value', 'selfishly ');

      cy.get(`[data-cy="contentEncoding"]`).type('after');
      cy.get(`[data-cy="contentEncoding"]`).should('have.value', 'after');

      cy.get(`[data-cy="compressionRatio"]`).type('8563.9');
      cy.get(`[data-cy="compressionRatio"]`).should('have.value', '8563.9');

      cy.get(`[data-cy="transferEncoding"]`).type('narrate');
      cy.get(`[data-cy="transferEncoding"]`).should('have.value', 'narrate');

      cy.get(`[data-cy="responseBodyHash"]`).type('clearly');
      cy.get(`[data-cy="responseBodyHash"]`).should('have.value', 'clearly');

      cy.get(`[data-cy="responseBodySample"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="responseBodySample"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="responseBodyValid"]`).should('not.be.checked');
      cy.get(`[data-cy="responseBodyValid"]`).click();
      cy.get(`[data-cy="responseBodyValid"]`).should('be.checked');

      cy.get(`[data-cy="responseBodyUncompressedBytes"]`).type('4572');
      cy.get(`[data-cy="responseBodyUncompressedBytes"]`).should('have.value', '4572');

      cy.get(`[data-cy="redirectDetails"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="redirectDetails"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="cacheControl"]`).type('incidentally lightly');
      cy.get(`[data-cy="cacheControl"]`).should('have.value', 'incidentally lightly');

      cy.get(`[data-cy="etag"]`).type('deadly well-worn');
      cy.get(`[data-cy="etag"]`).should('have.value', 'deadly well-worn');

      cy.get(`[data-cy="cacheAge"]`).type('18149');
      cy.get(`[data-cy="cacheAge"]`).should('have.value', '18149');

      cy.get(`[data-cy="cdnProvider"]`).type('slather help deplore');
      cy.get(`[data-cy="cdnProvider"]`).should('have.value', 'slather help deplore');

      cy.get(`[data-cy="cdnPop"]`).type('though lik');
      cy.get(`[data-cy="cdnPop"]`).should('have.value', 'though lik');

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
