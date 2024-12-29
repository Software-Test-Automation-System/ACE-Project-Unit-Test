import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

interface FileNode {
  name: string;
  type: 'FILE' | 'DIRECTORY';
  path: string;
  children?: FileNode[];
}

@Injectable({
  providedIn: 'root'
})
export class TestServiceService {
  private baseUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) { }

  unitTestService(githubURL: string): Observable<any> {
    const body = {
      githubURL
    };
    return this.http.post(`${this.baseUrl}/clone`, body);
  }

   getFileStructure(path: string): Observable<FileNode[]> {
    // Don't encode the path - let HttpClient handle it
    return this.http.get<FileNode[]>(`${this.baseUrl}/files`, {
      params: new HttpParams().set('path', path)
    });
  }

  getFileContent(path: string): Observable<string> {
    return this.http.get(`${this.baseUrl}/file-content`, {
      params: new HttpParams().set('path', path),
      responseType: 'text'
    });
  }
}