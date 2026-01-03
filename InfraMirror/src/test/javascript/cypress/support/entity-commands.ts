/**
 * Reusable Cypress commands for entity CRUD operations
 */

declare global {
  namespace Cypress {
    interface Chainable {
      /**
       * Test delete modal workflow
       * @param deleteButtonSelector - Selector for delete button
       * @param confirmButtonSelector - Selector for confirm button
       * @param entityName - Name of entity for logging
       */
      testDeleteModal(deleteButtonSelector: string, confirmButtonSelector: string, entityName?: string): Chainable<void>;

      /**
       * Test cancel delete operation
       * @param deleteButtonSelector - Selector for delete button
       */
      testCancelDelete(deleteButtonSelector: string): Chainable<void>;

      /**
       * Test side panel open/close
       * @param openButtonSelector - Selector for button that opens panel
       * @param panelTitle - Expected title in panel
       */
      testSidePanelOpenClose(openButtonSelector: string, panelTitle: string): Chainable<void>;

      /**
       * Test backdrop click closes panel
       */
      testBackdropClose(): Chainable<void>;

      /**
       * Test ESC key closes panel
       */
      testEscapeKeyClose(): Chainable<void>;

      /**
       * Fill and submit side panel form
       * @param formData - Object with field IDs and values
       */
      fillSidePanelForm(formData: Record<string, string>): Chainable<void>;

      /**
       * Cleanup test entities by name patterns
       * @param apiUrl - API endpoint (e.g., '/api/regions')
       * @param namePatterns - Array of name patterns to match
       */
      cleanupTestEntities(apiUrl: string, namePatterns: string[]): Chainable<void>;
    }
  }
}

// Delete modal test
Cypress.Commands.add('testDeleteModal', (deleteButtonSelector, confirmButtonSelector, entityName = 'entity') => {
  cy.get(deleteButtonSelector).last().click();
  cy.get('.modal').should('be.visible');
  cy.contains('Are you sure you want to delete').should('exist');
  cy.get(confirmButtonSelector).click();
  cy.wait('@deleteEntityRequest').then(({ response }) => {
    expect(response?.statusCode).to.equal(204);
  });
  cy.wait('@entitiesRequest').then(({ response }) => {
    expect(response?.statusCode).to.equal(200);
  });
  cy.log(`${entityName} deleted successfully`);
});

// Cancel delete test
Cypress.Commands.add('testCancelDelete', deleteButtonSelector => {
  cy.get(deleteButtonSelector).last().click();
  cy.get('.modal').should('be.visible');
  cy.get('body').then($body => {
    if ($body.find('.modal .btn-secondary').length > 0) {
      cy.get('.modal .btn-secondary').click();
    } else if ($body.find('.modal [data-cy="entityConfirmCancelButton"]').length > 0) {
      cy.get('.modal [data-cy="entityConfirmCancelButton"]').click();
    }
  });
  cy.get('.modal').should('not.exist');
});

// Side panel open/close test
Cypress.Commands.add('testSidePanelOpenClose', (openButtonSelector, panelTitle) => {
  cy.get(openButtonSelector).click();
  cy.get('.side-panel').should('be.visible');
  cy.contains('h5', panelTitle).should('exist');
  cy.get('.side-panel .btn-close').click();
  cy.get('.side-panel').should('not.exist');
});

// Backdrop close test
Cypress.Commands.add('testBackdropClose', () => {
  cy.get('.side-panel').should('be.visible');
  cy.get('.side-panel-overlay').click({ force: true });
  cy.get('.side-panel').should('not.exist');
});

// ESC key close test
Cypress.Commands.add('testEscapeKeyClose', () => {
  cy.get('.side-panel').should('be.visible');
  cy.get('body').type('{esc}');
  cy.get('.side-panel').should('not.exist');
});

// Fill side panel form (handles fixed positioning)
Cypress.Commands.add('fillSidePanelForm', formData => {
  Object.entries(formData).forEach(([fieldId, value]) => {
    cy.get(`#${fieldId}`).clear({ force: true }).type(value, { force: true });
  });
});

// Cleanup test entities
Cypress.Commands.add('cleanupTestEntities', (apiUrl, namePatterns) => {
  cy.authenticatedRequest({
    method: 'GET',
    url: `${apiUrl}?size=1000`,
  }).then(({ body }) => {
    const testEntities = body.filter(entity => {
      return namePatterns.some(pattern => {
        return (
          entity.name?.includes(pattern) ||
          entity.regionCode?.includes(pattern) ||
          entity.code?.includes(pattern) ||
          entity.displayName?.includes(pattern)
        );
      });
    });

    testEntities.forEach(entity => {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `${apiUrl}/${entity.id}`,
      });
    });

    cy.log(`Cleaned up ${testEntities.length} test entities`);
  });
});

export {};
