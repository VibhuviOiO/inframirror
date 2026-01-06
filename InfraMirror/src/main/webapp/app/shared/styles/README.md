# Generic Entity Styles Guide

## Overview

Generic, reusable SCSS styles for all entity pages (Region, Datacenter, Instance, Service, etc.) to maintain consistent design across the application.

## Files

### 1. `entity-common.scss`

Generic styles for entity list pages with table, search, pagination, and animations.

### 2. `side-panel-common.scss`

Generic styles for side panels (create/edit forms) with overlay, animations, and responsive layout.

## Usage

### Styles are Global

The generic styles are imported in `custom.scss` and available everywhere. **No need to import or create entity-specific SCSS files.**

### Use Generic Classes in Your Component

```tsx
export const Datacenter = () => {
  return (
    <div className="entity-page">
      <div className="card shadow-sm border-0 entity-card">
        <Table className="table-hover mb-0 entity-table">{/* Table content */}</Table>
      </div>
    </div>
  );
};
```

## Available Classes

**Entity Page:**

- `.entity-page` - Main wrapper
- `.entity-card` - Card container
- `.entity-table` - Table with hover effects
- `.search-input` - Rounded search input
- `.action-btn` - Button with hover lift
- `.entity-badge` - Styled badge
- `.pagination-info` - Pagination counter
- `.loading-icon` - Animated spinner

**Side Panel:**

- `.side-panel-overlay` - Dark backdrop
- `.side-panel` - Sliding panel
- `.side-panel-header` - Panel header
- `.side-panel-body` - Scrollable content
- `.side-panel-footer` - Action buttons

## Example: New Entity

```tsx
// datacenter.tsx - NO SCSS import needed!
import React from 'react';
import { Table, Button, Input } from 'reactstrap';

export const Datacenter = () => {
  return (
    <div className="entity-page">
      <div className="card shadow-sm border-0 entity-card">
        <div className="card-body">
          <h4>Datacenters</h4>
          <Input placeholder="Search..." className="search-input" />
          <Button className="action-btn">Create</Button>
        </div>
        <Table className="entity-table">
          <tbody>
            <tr>
              <td>
                <span className="entity-badge">Active</span>
              </td>
            </tr>
          </tbody>
        </Table>
      </div>
    </div>
  );
};
```

```tsx
// datacenter-side-panel.tsx - NO SCSS import needed!
export const DatacenterSidePanel = ({ isOpen, onClose }) => (
  <>
    {isOpen && (
      <>
        <div className="side-panel-overlay" onClick={onClose} />
        <div className={`side-panel ${isOpen ? 'open' : ''}`}>
          <div className="side-panel-header">
            <h5>Create Datacenter</h5>
          </div>
          <div className="side-panel-body">{/* Form */}</div>
          <div className="side-panel-footer">
            <Button>Save</Button>
          </div>
        </div>
      </>
    )}
  </>
);
```

## Benefits

1. **No SCSS files needed** - Styles are global
2. **Consistency** - All entities look the same
3. **Maintainability** - Update once, affects all
4. **Faster development** - Just use the classes

## Reference

See `src/main/webapp/app/entities/region/region.tsx` for a complete example.
