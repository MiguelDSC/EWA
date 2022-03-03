import { Pipe, PipeTransform } from '@angular/core';
import { Note } from '../../../models/note';

// Define pipe name for search function
@Pipe({
  name: 'noteFilter',
})
export class filterdNotesPipe implements PipeTransform {
  transform(notes: Note[], searchTerm: string): Note[] {
    //if there is no searchterm, return normal notes list
    if (!searchTerm || searchTerm === '') {
      return notes;
    }

    const lowercasedSearchTerm = searchTerm.toLowerCase();
    // Return found notes with searchTerm in title or content
    return notes.filter(
      ({ title, content }) =>
        //cast to lowercase and check if in notes list
        title.toLowerCase().includes(lowercasedSearchTerm) ||
        content.toLowerCase().includes(lowercasedSearchTerm)
    );
  }
}
