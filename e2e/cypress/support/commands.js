Cypress.Commands.add('loginPatient', () => {
    cy.visit('/#/login');
    cy.get('form .form-group #inputEmail').type('chris.anger@email.com');
    cy.get('input[name=password]').type('AngerManagement');
    cy.contains('button span', ' Login ').click();
    cy.get('div.user-name').should('contain', 'Chris A');
})

Cypress.Commands.add('loginDoctor', () => {
    cy.visit('/#/login');
    cy.get('form .form-group #inputEmail').type('doctor.eggman@email.com');
    cy.get('input[name=password]').type('ChaosEmeralds');
    cy.contains('button span', ' Login ').click();
    cy.get('div.user-name').should('contain', 'Doctor E');
})

Cypress.Commands.add('logout', () => {
    cy.get('button span .user-info').click();
    cy.wait(500);
    cy.get('button').contains('Logout').click();
})