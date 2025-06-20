import {Injectable} from '@angular/core';
import * as Papa from 'papaparse';

@Injectable({
  providedIn: 'root'
})
export class CsvConverterService {

  constructor() {
  }

  public parseCsv(file: File): Promise<any> {
    return new Promise((resolve, reject) => {
      Papa.parse(file, {
        header: true,
        complete: (result) => {
          resolve(result.data);
        },
        error: (error) => {
          reject(error);
        }
      });
    });
  }

  public parsCsvToOutPatientDepartment(file: File): Promise<any> {
    return new Promise((resolve, reject) => {
      Papa.parse(file, {
        header: true,
        dynamicTyping: true,
        skipEmptyLines: true,
        complete: (result) => {

          try {
            const data = result.data.map(entry => {
              if (entry.openingHours) {
                entry.openingHours = JSON.parse(entry.openingHours);
              }
              return entry;
            });
            resolve(data);
          } catch (err) {
            console.error('Error parsing JSON:', err.message);
            reject(err);
          }
        },
        error: (err) => {
          console.error('Error parsing CSV:', err);
          reject(err);
        }
      });
    });

  }


}
