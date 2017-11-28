import { Injectable } from '@angular/core';
import {Headers, Http, RequestOptions} from "@angular/http";
import { AppConfigConsts } from  './../../config';
import 'rxjs/add/operator/map';
import { UserMessagesRequest, MessageType, MessagesAggregate, ResponseFromStegHash, ImageOption
, PublishMessage} from './messaging.model';
import {Message} from "../../components/home/home.model";
import {hasCommentAfterPosition} from "tslint";

@Injectable()
export class MessagingService {

  private static API_URL: string = AppConfigConsts.serwerUrl;

  constructor(private http: Http) {
  }

  getUserMessagesReceived(username: string, pageNumber: number, callback) : void {
    this.performRequest(username, pageNumber, MessageType.RECEIVED, callback);
  }

  getUserMessagesSent(username: string, pageNumber: number, callback) : void {
    this.performRequest(username, pageNumber, MessageType.SENT, callback);
  }

  private performRequest(username: string, pageNumber: number, messageType: MessageType, callback) : void {
    let userMessageRequest: UserMessagesRequest = {
      username: username,
      pageNumber: pageNumber,
      messageType: messageType
    }

    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    const options = new RequestOptions({headers: headers});

    const url: string = MessagingService.API_URL + "/messages";

    this.http.post(url, JSON.stringify(userMessageRequest), options)
      .map(res => res.json())
      .subscribe(
        (responseFromServer) => {
          callback(responseFromServer);
        },
        (error) => {
          let errResp: ResponseFromStegHash =  error;
          let response: MessagesAggregate = {
            lastPageNumber: 1,
            currentPageNumber: 1,
            messages: new Array<Message>()
          };
          alert(errResp.status);
          callback(response);
        });
  }

  publishMessage(to: string[], message: string, hashtags: string[], callback) : void {
    let publishMessage: PublishMessage = {
      from: localStorage.getItem('currentUserName'),
      to: to,
      message: message,
      hashtags: hashtags,
      imageOption: ImageOption.RANDOM
    }

    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    const options = new RequestOptions({headers: headers});

    const url: string = MessagingService.API_URL + "/publish";

    this.http.post(url, JSON.stringify(publishMessage), options)
      .map(res => res.json())
      .subscribe(
        (responseFromServer) => {
          console.log(responseFromServer.data);
          callback("Message published!");
        },
        (error) => {
          let errResp: ResponseFromStegHash =  error.data;
          console.log(errResp);
          callback("Problem with publishing message.");
        });
  }

}
