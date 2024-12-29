import { NgClass, NgIf, NgFor } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

interface FileNode {
  name: string;
  type: 'FILE' | 'DIRECTORY';
  path: string;
  children?: FileNode[];
  expanded?: boolean;
}

@Component({
  selector: 'app-file-node',
  standalone: true,
  imports: [NgClass, NgIf, NgFor],
  styleUrl: './file-node.component.css',
  template: `
    <div class="tree-item" [class.expanded]="node.expanded">
      <div class="node" [class.folder]="node.type === 'DIRECTORY'" [class.file]="node.type === 'FILE'"
           (click)="toggleNode()">
        <span class="node-content">
          <i class="bx" [ngClass]="{'bx-folder': node.type === 'DIRECTORY', 'bx-file': node.type === 'FILE'}"></i>
          {{ node.name }}
        </span>
      </div>
      <div class="children" *ngIf="node.children && node.expanded">
        <app-file-node 
          *ngFor="let child of node.children"
          [node]="child"
          (onSelect)="onSelect.emit($event)">
        </app-file-node>
      </div>
    </div>
  `
})
export class FileNodeComponent {
  @Input() node!: FileNode;
  @Output() onSelect = new EventEmitter<FileNode>();

  toggleNode() {
    if (this.node.type === 'DIRECTORY') {
      this.node.expanded = !this.node.expanded;
    } else {
      this.onSelect.emit(this.node);
    }
  }
}