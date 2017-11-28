import { Component, OnInit } from '@angular/core';
import {AuthorizationService} from "../../services/authorization/authorization.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  password: string;
  username: string;

  constructor(private authorizationService: AuthorizationService) {}

  ngOnInit() {
  }


  loginUser() {
    if (this.username === "" || this.password === ""){
      alert('No credentials!');
    } else{
      if (this.authorizationService.login(this.username, this.password)){
        console.log('Successful login.');
      } else{
        //console.log('Wrong credentials!');
      }
    }
  }
}
