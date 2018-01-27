import i18n = require('i18next');
import {MechanismView} from '../mechanism_view';
import {mechanismTypes, applicationId, apiEndpointRepository} from '../../js/config';
import {DialogView} from './dialog_view';
import {Configuration} from '../../models/configurations/configuration';
import {TextView} from '../text/text_view';
import {RatingView} from '../rating/rating_view';
import {AudioView} from '../audio/audio_view';
import {AttachmentView} from '../attachment/attachment_view';
import {RatingMechanism} from '../../models/mechanisms/rating_mechanism';
import {AttachmentMechanism} from '../../models/mechanisms/attachment_mechanism';
import {ScreenshotView} from '../screenshot/screenshot_view';
import {PageNavigation} from '../../js/helpers/page_navigation';
import {PaginationContainer} from '../pagination_container';
import {ConfigurationInterface} from '../../models/configurations/configuration_interface';
import {Feedback} from '../../models/feedbacks/feedback';
import {CategoryView} from '../category/category_view';
import {AudioFeedback} from '../../models/feedbacks/audio_feedback';
import {ContextInformation} from '../../models/feedbacks/context_information';
import {FeedbackService} from '../../services/feedback_service';
import {PageNotification} from '../page_notification';
import {GeneralConfiguration} from '../../models/configurations/general_configuration';
import {InfoView} from '../info/info_view';
import {InfoMechanism} from '../../models/mechanisms/info_mechanism';
import {CategoryMechanism} from '../../models/mechanisms/category_mechanism';
import {QuestionDialogView} from './question_dialog_view';


/**
 * Acts as a wrapper to the jquery UI dialog
 */
export class FeedbackDialogView extends DialogView {
    mechanismViews:MechanismView[];
    pageNavigation:PageNavigation;
    paginationContainer:PaginationContainer;
    audioView:AudioView;

    constructor(public dialogId:string, public template:any, public configuration:Configuration, public context:any, public openCallback?:() => void,
                public closeCallback?:() => void) {
        super(dialogId, template, context, openCallback, closeCallback);
        this.dialogContext = $.extend({}, this.dialogContext, this.configuration.getContext());
        this.initMechanismViews();
        this.configurePageNavigation();
    }

    initDialog() {
        let myThis = this,
            dialogContainer = jQuery('#' + this.dialogId);
        super.initDialog();
        this.dialogElement.dialog('option', 'position', {
            my: this.dialogContext.dialogPositionMy,
            at: this.dialogContext.dialogPositionAt,
            of: this.dialogContext.dialogPositionOf
        });

        dialogContainer.find('.discard-feedback').on('click', function () {
            myThis.discardFeedback();
        });
    }

    initMechanismViews() {
        this.mechanismViews = [];

        for (let textMechanism of this.configuration.getActiveMechanismConfig(mechanismTypes.textType)) {
            this.mechanismViews.push(new TextView(textMechanism, this.dialogId));
        }

        for (let ratingMechanism of this.configuration.getActiveMechanismConfig(mechanismTypes.ratingType)) {
            this.mechanismViews.push(new RatingView(<RatingMechanism>ratingMechanism, this.dialogId));
        }

        for (let categoryMechanism of this.configuration.getActiveMechanismConfig(mechanismTypes.categoryType)) {
            this.mechanismViews.push(new CategoryView(<CategoryMechanism>categoryMechanism));
        }

        for (let screenshotMechanism of this.configuration.getActiveMechanismConfig(mechanismTypes.screenshotType)) {
            let screenshotView = this.initScreenshot(screenshotMechanism, this.dialogId);
            this.mechanismViews.push(screenshotView);
        }

        let audioMechanism = this.configuration.getActiveMechanismConfig(mechanismTypes.audioType)[0];
        if (audioMechanism) {
            let audioContainer = $("#" + this.dialogId + " #audioMechanism" + audioMechanism.id);
            this.audioView = new AudioView(audioMechanism, audioContainer, this.dialogContext.distPath);
            this.mechanismViews.push(this.audioView);
        }

        for (let attachmentMechanism of this.configuration.getActiveMechanismConfig(mechanismTypes.attachmentType)) {
            this.mechanismViews.push(new AttachmentView(<AttachmentMechanism>attachmentMechanism, this.dialogId, this.dialogContext.distPath));
        }

        for (let infoMechanism of this.configuration.getActiveMechanismConfig(mechanismTypes.infoType)) {
            this.mechanismViews.push(new InfoView(<InfoMechanism>infoMechanism, this.dialogId));
        }

        this.addEvents(this.dialogId, this.configuration);
    }

    configurePageNavigation() {
        let myThis = this;
        this.pageNavigation = new PageNavigation(this.configuration, jQuery('#' + this.dialogId));
        this.paginationContainer = new PaginationContainer(jQuery('#' + this.dialogId + '.feedback-container .pages-container'), this.pageNavigation, (changedPageNumber) => {
            myThis.changeDialogTitle(changedPageNumber);
        });
    }

    changeDialogTitle(pageNumber:number) {
        if(this.context.localesOverride && this.context.localesOverride.dialog && this.context.localesOverride.dialog.dialog && this.context.localesOverride.dialog.dialog.titles) {
            if(this.context.localesOverride.dialog.dialog.titles[pageNumber]) {
                this.dialogElement.dialog('option', 'title', this.context.localesOverride.dialog.dialog.titles[pageNumber]);
            } else {
                this.dialogElement.dialog('option', 'title', this.dialogContext.dialogTitle);
            }
        }
    }

    addEvents(containerId, configuration:ConfigurationInterface) {
        let myThis = this;
        let generalConfiguration = configuration.generalConfiguration;
        var container = $('#' + containerId);
        var textareas = container.find('textarea.text-type-text');
        var textMechanisms = configuration.getMechanismConfig(mechanismTypes.textType);
        var feedbackDialogView = this;

        var feedbackService = new FeedbackService(this.context.apiEndpointRepository, this.dialogContext.lang);

        container.find('button.submit-feedback').unbind().on('click', function (event) {
            event.preventDefault();
            event.stopPropagation();
            var submitButton= $(this);
            submitButton.prop('disabled', true);
            submitButton.text(submitButton.text() + '...');


            if(!myThis.ratingMechanismsAreValid(container)) {
                submitButton.prop('disabled', false);
                submitButton.text(submitButton.text().replace(/...$/,''));
                return;
            }

            if(!myThis.categoryMechanismsAreValid(container)) {
                submitButton.prop('disabled', false);
                submitButton.text(submitButton.text().replace(/...$/,''));
                return;
            }

            // TODO adjust
            // validate anyway before sending
            if (textMechanisms.length > 0) {
                textareas.each(function () {
                    $(this).validate();
                });


                var invalidTextareas = container.find('textarea.text-type-text.invalid');
                if (invalidTextareas.length == 0) {
                    feedbackDialogView.prepareFormData(configuration, function (formData) {
                        feedbackDialogView.sendFeedback(feedbackService, formData, generalConfiguration);
                    });
                } else {
                    submitButton.prop('disabled', false);
                    submitButton.text(submitButton.text().replace(/...$/,''));
                }
            } else {
                feedbackDialogView.prepareFormData(configuration, function (formData) {
                    feedbackDialogView.sendFeedback(feedbackService, formData, generalConfiguration);
                });
            }
        });
    };

    ratingMechanismsAreValid(container:any):boolean {
        let valid = true;
        container.find('.review-page-mechanisms .rating-type.mandatory .rating-input').each(function() {
            if(parseInt(jQuery(this).starRating('getRating')) === 0) {
                valid = false;
                let errorMessage = jQuery(this).data('mandatory-message');
                jQuery(this).append('<span class="feedback-form-error">' + errorMessage + '</span>');
            }
        });

        return valid;
    }

    categoryMechanismsAreValid(container:any):boolean {
        container.find('.review-page-mechanisms .category-type.mandatory').validateCategory();

        if(container.find('.review-page-mechanisms .category-type.mandatory.invalid').length > 0) {
            return false;
        } else {
            return true;
        }
    }

    sendFeedback(feedbackService:FeedbackService, formData:any, generalConfiguration:GeneralConfiguration) {
        let myThis = this;
        var feedbackDialogView = this;
        var url = this.context.apiEndpointRepository + 'feedback_repository/' + this.context.lang + '/applications/' + this.context.applicationId + '/feedbacks/';

        feedbackService.sendFeedback(url, formData, function(data) {
            if(generalConfiguration && generalConfiguration.getParameterValue('successDialog')) {
                feedbackDialogView.discardFeedback();
                feedbackDialogView.paginationContainer.showFirstPage();
                let dialogTemplate = require('../../templates/info_dialog.handlebars');
                let successMessage = generalConfiguration.getParameterValue('successMessage') || i18n.t('general.success_message');
                let successDialogId = 'infoDialog';
                jQuery('#' + successDialogId).remove();
                let successDialogView = new QuestionDialogView(successDialogId, dialogTemplate, {'message': <string>successMessage});
                successDialogView.setTitle(<string>i18n.t('general.success_dialog_title'));
                successDialogView.setModal(true);
                successDialogView.addAnswerOption('#infoDialogOkay', function() {
                    successDialogView.close();
                });
                successDialogView.open();
            } else if (generalConfiguration && generalConfiguration.getParameterValue('closeDialogOnSuccess')) {
                feedbackDialogView.discardFeedback();
                feedbackDialogView.paginationContainer.showFirstPage();
                PageNotification.show(<string>i18n.t('general.success_message'));
            } else {
                feedbackDialogView.resetDialog();
                $('.server-response').addClass('success').text(i18n.t('general.success_message'));
            }
            myThis.dialogElement.find('button.submit-feedback').prop('disabled', false);
            myThis.dialogElement.find('button.submit-feedback').text(myThis.dialogElement.find('button.submit-feedback').text().replace(/...$/,''));
        }, function(error) {
            myThis.dialogElement.find('.server-response').addClass('error').text('Failure: ' + JSON.stringify(error));
            myThis.dialogElement.find('button.submit-feedback').prop('disabled', false);
            myThis.dialogElement.find('button.submit-feedback').text(myThis.dialogElement.find('button.submit-feedback').text().replace(/...$/,''));
        });
    }

    /**
     * Creates the multipart form data containing the data of the active mechanisms.
     */
    prepareFormData(configuration:ConfigurationInterface, callback?:any) {
        // TODO refactoring: the mechanism views should return their feedback data
        var dialogView = this;
        var formData = new FormData();
        var audioMechanisms = configuration.getMechanismConfig(mechanismTypes.audioType);
        var hasAudioMechanism = audioMechanisms.filter(audioMechanism => audioMechanism.active === true).length > 0;

        dialogView.resetMessageView();

        var feedbackObject = new Feedback('Feedback', this.dialogContext.userId, this.dialogContext.language, this.context.applicationId, configuration.id, [], [], [], [], null, [], []);
        feedbackObject.contextInformation = ContextInformation.create(this.context.metaData);

        for (var mechanismView of dialogView.mechanismViews) {
            if (mechanismView instanceof TextView) {
                feedbackObject.textFeedbacks.push(mechanismView.getFeedback());
            } else if (mechanismView instanceof RatingView) {
                feedbackObject.ratingFeedbacks.push(mechanismView.getFeedback());
            } else if (mechanismView instanceof AttachmentView) {
                feedbackObject.attachmentFeedbacks = mechanismView.getFeedbacks();
                for (let i = 0; i < mechanismView.getFiles().length; i++) {
                    let file = mechanismView.getFiles()[i];
                    formData.append(mechanismView.getPartName(i), file, file.name);
                }
            } else if (mechanismView instanceof ScreenshotView) {
                let screenshotBinary = mechanismView.getScreenshotAsBinary();
                if (screenshotBinary !== null) {
                    feedbackObject.screenshotFeedbacks.push(mechanismView.getFeedback());
                    formData.append(mechanismView.getPartName(), mechanismView.getScreenshotAsBinary(), 'weblib_screenshot_' + this.context.userId + '.png');
                }
            } else if (mechanismView instanceof CategoryView) {
                for(let categoryFeedback of mechanismView.getCategoryFeedbacks()) {
                    feedbackObject.categoryFeedbacks.push(categoryFeedback);
                }
            }
        }

        // TODO assumes only one audio mechanism --> support multiple
        for (var audioMechanism of audioMechanisms.filter(mechanism => mechanism.active === true)) {
            let partName = "audio" + audioMechanism.id;
            var audioElement = jQuery('section#audioMechanism' + audioMechanism.id + ' audio')[0];
            if (!audioElement || Fr.voice.recorder === null) {
                formData.append('json', new Blob([JSON.stringify(feedbackObject)], { type: 'application/json' }));
                callback(formData);
            }

            try {
                var duration = Math.ceil(audioElement.duration === undefined || audioElement.duration === 'NaN' ? 0 : audioElement.duration);
                if (duration === 0) {
                    hasAudioMechanism = false;
                    break;
                }
                var audioFeedback = new AudioFeedback(partName, duration, "wav", audioMechanism.id);
                this.audioView.getBlob(function (blob) {
                    var date = new Date();
                    formData.append(partName, blob, "recording" + audioMechanism.id + "_" + date.getTime());
                    feedbackObject.audioFeedbacks.push(audioFeedback);
                    formData.append('json', new Blob([JSON.stringify(feedbackObject)], { type: 'application/json' }));
                    callback(formData);
                });
            } catch (e) {
                formData.append('json', new Blob([JSON.stringify(feedbackObject)], { type: 'application/json' }));
                callback(formData);
            }
        }

        if (!hasAudioMechanism) {
            formData.append('json', new Blob([JSON.stringify(feedbackObject)], { type: 'application/json' }));
            callback(formData);
        }
    };

    initScreenshot(screenshotMechanism, containerId):ScreenshotView {
        if (screenshotMechanism == null) {
            return;
        }

        var elementToCaptureSelector = 'body';
        if (screenshotMechanism.getParameterValue('elementToCapture') !== null && screenshotMechanism.getParameterValue('elementToCapture') !== "") {
            elementToCaptureSelector = screenshotMechanism.getParameterValue('elementToCapture');
        }

        var container = $('#' + containerId);
        var dialogSelector = '[aria-describedby="' + containerId + '"]';

        var screenshotPreview = container.find('.screenshot-preview'),
            screenshotCaptureButton = container.find('button.take-screenshot'),
            elementToCapture = $('' + elementToCaptureSelector),
            elementsToHide = ['.ui-widget-overlay', dialogSelector, '.ui-dialog.feedback-dialog', '.' + this.context.dialogCSSClass];
        // TODO attention: circular dependency
        var screenshotView = new ScreenshotView(screenshotMechanism, screenshotPreview, screenshotCaptureButton,
            elementToCapture, container, this.dialogContext.distPath, elementsToHide, screenshotMechanism.getParameterValue('manipulationOnObject'));
        screenshotView.colorPickerCSSClass = this.dialogContext.colorPickerCSSClass;
        screenshotView.setDefaultStrokeWidth(this.dialogContext.defaultStrokeWidth);

        screenshotMechanism.setScreenshotView(screenshotView);
        return screenshotView;
    };

    resetDialog() {
        super.resetDialog();
        if (this.mechanismViews) {
            for (var mechanismView of this.mechanismViews) {
                mechanismView.reset();
            }
        }
    }

    discardFeedback() {
        this.resetDialog();
        this.close();
    }
}