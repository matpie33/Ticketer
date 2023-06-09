import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from "@angular/common/http";
import {Observable, of, tap} from "rxjs";

@Injectable()
export class CacheInterceptorService implements HttpInterceptor{
  private urlToResponseCache: Map<string, HttpResponse<any>> = new Map()
  constructor() { }
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.method != "GET"){
      return next.handle(request);
    }
    else{
      let valueFromCache = this.urlToResponseCache.get(request.url);
      if (valueFromCache){
        return of(valueFromCache.clone());
      }
      else{
        return next.handle(request).pipe(tap(
          responseEvent=>{
            if (responseEvent instanceof HttpResponse){
              this.urlToResponseCache.set(request.url, responseEvent as HttpResponse<any>);
            }
        }));


      }
    }
  }
}
