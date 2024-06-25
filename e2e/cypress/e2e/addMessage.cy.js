describe('Login Test', () => {
    it('should navigate to login page, enter credentials and submit', () => {
        // Open the website

        // Navigate to login page
        cy.visit('/#/login');

        cy.wait(1000);

        // Enter email
        cy.get('form .form-group #inputEmail').type('chris.anger@email.com');

        // Enter password
        cy.get('input[name=password]').type('AngerManagement');

        // Submit the form
        cy.contains('button span', ' Login ').click();

        cy.wait(1000);

        cy.get('div.user-name').should('contain', 'Chris A');
    });
});