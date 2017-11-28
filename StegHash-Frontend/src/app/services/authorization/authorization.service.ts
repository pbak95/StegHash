import { Injectable } from '@angular/core';
import {Authorize } from './authorization.model';
import {User} from './authorization.model';
import {Headers, Http, RequestOptions} from "@angular/http";
import {Router} from "@angular/router";
import { AppConfigConsts } from  './../../config';
import 'rxjs/add/operator/map';

@Injectable()
export class AuthorizationService {

  authorize: Authorize;
  token: string;
  username: string;

  constructor(private http: Http, private router: Router) {
    this.authorize = {active: false};
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    if (currentUser) {
      this.token = currentUser.token;
      const user = JSON.parse(localStorage.getItem('currentUserName'));
      this.username = user.username;
      this.authorize.active = true;
    }
  }


  login(username: string, password: string) : boolean {
    const url = AppConfigConsts.serwerUrl + '/login';

    const user: User = {
      username: username,
      password: password
    };

    //const encoded = btoa('pik-webapp-client:secret');

    const headers = new Headers();
    //headers.append('Authorization', 'Basic ' + encoded);
    headers.append('Content-Type', 'application/json');
    //headers.append('Content-Type', 'application/x-www-form-urlencoded');

    const options = new RequestOptions({headers: headers});

    this.http.post(url, JSON.stringify(user), options)
      .map(res => res.json())
      .subscribe((userFromServer) => {
          this.username = userFromServer.username;
          this.authorize.active = true;
        localStorage.setItem('currentUserName', this.username);
        this.router.navigate(['./home']);
        });

    // this.http.post(url, undefined, options)
    //   .map(res => res.json())
    //   .subscribe(access_token => {
    //       this.token = access_token.access_token;
    //       this.username = username;
    //       this.authorize.active = true;
    //       this.router.navigate(['./eBay']);
    //       localStorage.setItem('currentUser', JSON.stringify({token: this.token}));
    //       localStorage.setItem('currentUserName', JSON.stringify({username: username}));
    //     },
    //     error2 => {
    //       console.log("Wrong credentials");
    //     });
    if(this.authorize.active)
      return true;
    else
      return false;
  }
}
