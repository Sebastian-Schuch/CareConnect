describe('Login Test', () => {
    it('should navigate to login page, enter credentials and submit', () => {
        cy.loginPatient();
    });
});

describe('Start Telemedicine Check', () => {
    it('should navigate to telemedicine page, open new chat and write one message', () => {
        cy.loginPatient();
        cy.visit('/#/home/patient/telemedicine');
        cy.contains('button','Open New Chat').click();
        cy.get('#treatmentSelect').click();
        cy.wait(100);
        cy.get('mat-option').click();
        cy.contains('button','Create').click();
        cy.get('mat-list-item').first().click();
        cy.wait(1000);
        cy.get('input[placeholder="Type a message"]').type('Hello{enter}');
        cy.contains('.message-content', 'Hello').should('exist');
        cy.logout();
        cy.loginDoctor();
        cy.visit('/#/home/doctor/telemedicine');
        cy.get('mat-list-item').first().click();
        cy.contains('.message-content', 'Hello').should('exist');
    });
});