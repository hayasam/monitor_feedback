"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
/**
 * Created by flo on 14.07.16.
 */
var core_1 = require('@angular/core');
var http_1 = require('@angular/http');
require('rxjs/add/operator/map');
var Observable_1 = require('rxjs/Observable');
var ServerConfiguration_1 = require('./ServerConfiguration');
var FeedbackService = (function () {
    function FeedbackService(http, configuration) {
        this.http = http;
        this.configuration = configuration;
        this.feedbackListEvent = new core_1.EventEmitter();
        this.selectedFeedbackEvent = new core_1.EventEmitter();
        this.url = configuration.ServerWithApiUrl;
        this.headers = new http_1.Headers();
        this.headers.append('Accept', 'application/json');
    }
    FeedbackService.prototype.GetFeedbacks = function (application) {
        var _this = this;
        return this.http.get(this.url + application + "/feedbacks")
            .map(function (response) { return response.json(); })
            .subscribe(function (data) { return _this.feedbackListEvent.emit(data); });
    };
    FeedbackService.prototype.SelectFeedback = function (feedback) {
        this.selectedFeedbackEvent.emit(feedback);
    };
    FeedbackService.prototype.handleError = function (error) {
        console.error(error);
        return Observable_1.Observable.throw(error.json().error || 'Server error');
    };
    FeedbackService = __decorate([
        core_1.Injectable(), 
        __metadata('design:paramtypes', [http_1.Http, ServerConfiguration_1.ServerConfiguration])
    ], FeedbackService);
    return FeedbackService;
}());
exports.FeedbackService = FeedbackService;
//# sourceMappingURL=feedback.service.js.map