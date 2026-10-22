import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterRequest } from '../models/register-request';
import {AuthenticationResponse} from "../models/authentication-response";
import {AuthenticationRequest} from "../models/authentication-request";
import { HttpClient } from '@angular/common/http';
import { VerificationRequest } from '../models/verification-request';
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
private baseUrl:string='http://localhost:8080/api/v1/auth'

  constructor(
    private http: HttpClient) { }
    register(registerRequest: RegisterRequest): Observable<AuthenticationResponse> {
      return this.http.post<AuthenticationResponse>(`${this.baseUrl}/register`, registerRequest);
    }
  login(
    authRequest: AuthenticationRequest){
      return this.http.post<AuthenticationResponse>
      (`${this.baseUrl}/authenticate`, authRequest);
    }
  verifyCode(verificationRequest: VerificationRequest){
 return this.http.post<AuthenticationResponse>(`${this.baseUrl}/verify`, verificationRequest)
    }

forgotPassword(email: string) {
return this.http.post<void>(`${this.baseUrl}/forgot-password`, { email }, {
    responseType: 'text' as 'json' // ✅ magic fix ici
  });}
resetPassword(email: string, code: string, newPassword: string) {
  return this.http.post(`${this.baseUrl}/reset-password`, {
    email,
    code,
    newPassword
  });
}
getUserRole(): string {
  return localStorage.getItem('role') || '';
}

}
