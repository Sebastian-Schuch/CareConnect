context('add message', () => {
    let msgText = 'msg' + new Date().getTime();

    it.skip('create message', () => {
        cy.loginAdmin();
        cy.createMessage(msgText);
    })

});
