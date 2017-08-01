describe('JQuery Feedback Plugin', function () {
    beforeEach(function () {
        browser.ignoreSynchronization = true;
        browser.get('/');
    });
    it('should open a dialog on link click', function () {
        var feedbackEntryLink = browser.element(by.css('a#feedbackEntryPoint'));
        feedbackEntryLink.click();
        browser.sleep(2000);
        expect(feedbackEntryLink.isPresent()).toEqual(true);
        expect(feedbackEntryLink).toBeDefined();
        var feedbackDialog = browser.element(by.css('section#feedbackContainer'));
        expect(feedbackDialog.isPresent()).toEqual(true);
    });
});
//# sourceMappingURL=feedback_plugin.e2e.js.map