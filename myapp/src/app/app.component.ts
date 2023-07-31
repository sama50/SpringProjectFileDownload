import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs'
import { WebSocketSubject, webSocket } from 'rxjs/webSocket';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent  implements OnInit, OnDestroy  {

  private readonly serverUrl = 'ws://localhost:8080/ws'; // WebSocket URL
  private socket!: WebSocketSubject<any>;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.initializeWebSocket();
  }

  ngOnDestroy(): void {
    this.socket.complete();
  }

  initializeWebSocket(): void {
    this.socket = webSocket(this.serverUrl);
    console.log("connected");
    this.socket.subscribe(
      (message) => {
        console.log('WebSocket Message:', message);
        // Here, you can handle WebSocket messages received from the server
      },
      (error) => {
        console.error('WebSocket Error:', error);
      },
      () => {
        console.log('WebSocket Connection Closed');
      }
    );
  }

  showWebSocketMessageAlert(message: any): void {
    alert(message);
  }
    
  // downloadFile(): void {
  //   // this.socket.next('File download completed');
  //   this.http.get('http://localhost:8080/api/downloadlargefile', { responseType: 'blob' })
  //     .subscribe((response: Blob) => {
  //       const url = window.URL.createObjectURL(new Blob([response]));
  //       const a = document.createElement('a');
  //       a.href = url;
  //       a.download = 'offline_file.zip';
  //       a.click();
  //       window.URL.revokeObjectURL(url);
  //     });
  // }
  downloadFile(): void {
    // Send the request to the server and get the file as a blob
    window.open('http://localhost:8080/api/downloadlargefile', '_blank');
    // this.socket.next('File download completed'); // Send a WebSocket message after the file download is complete

  }
  
  
}
