const sidebarSelector = '.sidebar';
const sidebarItemSelector = '.sidebar-item';

Cypress.Commands.add('clickOnSidebarMenuItem', (menuName: string) => {
  cy.get(sidebarSelector).find(sidebarItemSelector).contains(menuName).click();
});