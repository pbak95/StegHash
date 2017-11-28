import { Component, OnInit } from '@angular/core';
import {AuthorizationService} from "./services/authorization/authorization.service";
import {Router} from "@angular/router";



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  ngOnInit() {
  }

  constructor(private authorizationService: AuthorizationService, private router: Router){}

  logout() {
    console.log("Logout!");
  }

  createMessage() {
    this.router.navigate(['./create']);
  }

}
