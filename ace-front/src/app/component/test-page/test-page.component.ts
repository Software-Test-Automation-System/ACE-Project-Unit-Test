import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { LoginServiceService } from '../../services/login-service.service';
import { TestServiceService } from '../../services/test-service.service';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { FileNodeComponent } from '../file-node/file-node.component';


interface FileNode {
  name: string;
  type: 'FILE' | 'DIRECTORY';
  path: string;
  children?: FileNode[];
  expanded?: boolean;
  
}

@Component({
  selector: 'app-test-page',
  standalone: true,
  imports: [FormsModule,NgIf,NgFor,NgClass,FileNodeComponent],
  templateUrl: './test-page.component.html',
  styleUrls: ['./test-page.component.css']
})
export class TestPageComponent {
  javaClass: string = '';
  constructor(private testService: TestServiceService) {}
  junit: string = '';
  fileStructure: FileNode[] = [];
  selectedFileContent: string = '';
  selectedFileName: string = '';

  toggleFolder(item: FileNode) {
    if (item.type === 'DIRECTORY') {
      item.expanded = !item.expanded;
    } else {
      this.viewFileContent(item);
    }
  }
  viewFileContent(file: FileNode) {
    if (file.type === 'FILE') {
      this.selectedFileName = file.name;
      // Add console log for debugging
      console.log('Attempting to load file:', file.path);
      
      // Get full path from node's path property
      const fullPath = file.path.startsWith('/') ? file.path.substring(1) : file.path;
      
      this.testService.getFileContent(fullPath).subscribe({
        next: (content: string) => {
          console.log('File content loaded successfully');
          this.selectedFileContent = content;
        },
        error: (error) => {
          console.error('Error loading file:', error);
          this.selectedFileContent = `Error loading file: ${file.path}\nError: ${error.message}`;
        }
      });
    }
  }


  onSubmit() {
    console.log('Submitting URL:', this.javaClass);
    this.testService.unitTestService(this.javaClass).subscribe({
      next: (response: any) => {
        console.log('Clone response:', response);
        if (response?.localPath) {
          this.loadFileStructure(response.localPath);
        }
      },
      error: (error) => {
        console.error('Error:', error);
        this.junit = 'Error occurred while generating test';
      },
    });
  }

  loadFileStructure(localPath: string) {
    console.log('Loading file structure for:', localPath);
    // Clean the path before sending
    const cleanPath = localPath.replace(/\\/g, '/');
    this.testService.getFileStructure(cleanPath).subscribe({
      next: (structure: FileNode[]) => {
        console.log('Received structure:', structure);
        this.fileStructure = this.initializeNodes(structure);
      },
      error: (error) => {
        console.error('Error loading file structure:', error);
      }
    });
  }

  initializeNodes(nodes: FileNode[]): FileNode[] {
    return nodes.map(node => ({
      ...node,
      expanded: false,
      children: node.children ? this.initializeNodes(node.children) : undefined
    }));
  }
  

  // Function to trigger file input visibility
  triggerFileInput() {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
      fileInput.click();
    }
  }

 

  // Function to handle file selection
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const reader = new FileReader();

      // Read the file content
      reader.onload = (e: ProgressEvent<FileReader>) => {
        this.javaClass = e.target?.result as string;
        console.log('File content:', this.javaClass);
      };

      reader.readAsText(file); // Read file as text
    }
  }
}
