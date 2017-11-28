import { Component, OnInit } from '@angular/core';
import {MessagingService} from "../../services/messaging/messaging.service";
import { AppConfigConsts } from  './../../config';


@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateComponent implements OnInit {

  to: string = "";
  message: string = "";
  hashtags: string = "";  //validation pattern for whole line:  ^#\w+( #\w+)*$
  hashtagsNumberNeeded: number = 0;
  validHashtagsNumber: boolean = false;
  validHashtagsFormat: boolean = false;

  constructor(private messagingService: MessagingService) {
  }

  ngOnInit() {
  }

  publishMessage() {
    if (this.to.length != 0 && this.message.length != 0
    && this.hashtags.length != 0) {
      let to: string[] = this.splitBySpace(this.to);
      let hashtags: string[] = this.splitBySpace(this.hashtags);
      this.messagingService.publishMessage(to, this.message, hashtags,
        (message, statusOK) => {
          alert(message);
          if (statusOK) {
            this.to = "";
            this.message = "";
            this.hashtags = "";
          }
        });
    } else {
      alert("Please fill the form.");
    }

  }

  calculateHashtagsNumber(message) {
    console.log('Message size: ' + message.length);
    if (message.length > 0) {
      let contentNumber: number = Math.ceil(message.length / AppConfigConsts.messageLength);
      console.log('Number of content needed: ' + contentNumber);
      let factorial: number = 1;
      let num: number = 1;
      this.hashtagsNumberNeeded = 1;
      while (factorial < contentNumber) {
        factorial = factorial * (++this.hashtagsNumberNeeded);
      }
    } else {
      this.hashtagsNumberNeeded = 0;
    }
    this.getStyle();
  }

  validateHashtags(hashtags) {
    let regex = /^#\w+( #\w+)*$/;
    this.validHashtagsFormat = regex.test(hashtags.trim());
    this.isValidForm();
  }

  private splitBySpace(phrase: string) : string[] {
    return phrase.trim().split(" ");
  }


  getStyle() {
    let hashtagsArr: string[] = this.splitBySpace(this.hashtags);
    let hastagsLength: number = hashtagsArr.length;
    if ((hastagsLength == 1 && !Boolean(hashtagsArr[0])) ||
      hastagsLength < this.hashtagsNumberNeeded) {
      this.validHashtagsNumber = false;
      this.isValidForm();
      return "red";
    } else {
      this.validHashtagsNumber = true;
      this.isValidForm();
      return "black";
    }
  }

  isValidForm() : boolean {
    return this.validHashtagsFormat && this.validHashtagsNumber;
  }


}
