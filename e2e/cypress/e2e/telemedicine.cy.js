describe('Start Telemedicine Check', () => {
    it('should navigate to telemedicine page, open new chat and write one message', () => {
        cy.loginPatient();
        cy.visit('/#/home/patient/telemedicine');
        cy.wait(500);
        cy.get('body').then(($body) => {
            if ($body.find('mat-list-item:contains("Treatment1")').length === 0) {
                cy.contains('button','Open New Chat').click();
                cy.wait(500);
                cy.get('#treatmentSelect').click();
                cy.wait(100);
                cy.get('mat-option').click();
                cy.contains('button','Create').click();
            }
        });
        cy.get('mat-list-item').contains('Treatment1').click();
        cy.wait(1000);
        cy.get('input[placeholder="Type a message"]').type('Hello{enter}');
        cy.contains('.message-content', 'Hello').should('exist');
        cy.logout();
        cy.loginDoctor();
        cy.visit('/#/home/doctor/telemedicine');
        cy.get('mat-list-item').contains('Treatment1').click();
        cy.contains('.message-content', 'Hello').should('exist');
    });
});