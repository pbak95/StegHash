import { Component, OnInit } from '@angular/core';
import { Message, MessagesAggregate } from './home.model';
import {MessagingService} from "../../services/messaging/messaging.service";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  messages:Message[];
  pages: number = 0; // is the maximum number
  currentIndex: number = 1;
  pagesIndex: Array<number>;
  pageStart: number = 1;
  loading: boolean = true;
  connectionError: boolean = false;



  constructor(private messagingService: MessagingService) {
    this.messages = [];
  }

  ngOnInit() {
    console.log('trigger init');
    this.messagingService.getUserMessagesReceived(
      localStorage.getItem('currentUserName'),this.pageStart, this.updateContent.bind(this)
    );

  }

  updateContent(receivedMessages: MessagesAggregate): void {
    if (receivedMessages.messages.length > 0) {
      this.messages = receivedMessages.messages;
      this.currentIndex = receivedMessages.currentPageNumber;
      this.pages = receivedMessages.lastPageNumber;
      this.pagesIndex =  this.fillArray();
    } else {
      this.connectionError = true;
    }
    this.loading = false;
  }


  fillArray(): any{
    let obj = new Array();
    for(let index = this.pageStart; index < this.pageStart + this.pages; index ++) {
      obj.push(index);
    }
    return obj;
  }
  refreshItems(){
    this.messagingService.getUserMessagesReceived(
      localStorage.getItem('currentUserName'),this.currentIndex, this.updateContent.bind(this)
    );
  }

  prevPage(){
    if(this.currentIndex > 1){
      this.currentIndex --;
      this.refreshItems();
    }
  }
  nextPage(){
    if(this.currentIndex < this.pages){
      this.currentIndex ++;
      this.refreshItems();
    }
  }
  setPage(index : number){
    this.currentIndex = index;
    this.refreshItems();
  }
}
