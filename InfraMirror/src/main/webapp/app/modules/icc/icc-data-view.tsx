import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Table, Spinner, Button, Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, Label, Input } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft, faCog } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

const ICCDataView = () => {
  const { code, resource } = useParams<{ code: string; resource: string }>();
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [columns, setColumns] = useState<Array<{ key: string; label: string }>>([]);
  const [configModalOpen, setConfigModalOpen] = useState(false);
  const [availableColumns, setAvailableColumns] = useState<Array<{ key: string; label: string; visible: boolean }>>([]);

  useEffect(() => {
    if (code && resource) {
      fetchData();
    }
  }, [code, resource]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/icc/proxy/${code}/${resource}`);
      const result = response.data;

      if (result.view?.columns) {
        const cols = result.view.columns;
        setAvailableColumns(cols);
        const visibleCols = cols.filter((c: any) => c.visible);
        setColumns(visibleCols.map((c: any) => ({ key: c.key, label: c.label })));
      } else if (result.data?.length > 0) {
        const allKeys = new Set<string>();
        result.data.forEach((item: any) => Object.keys(item).forEach(key => allKeys.add(key)));
        const cols = Array.from(allKeys).map(key => ({ key, label: key, visible: true }));
        setAvailableColumns(cols);
        setColumns(cols.map(c => ({ key: c.key, label: c.label })));
      }

      setData(result.data || []);
    } catch (error) {
      console.error('Error fetching data:', error);
      setData([]);
    } finally {
      setLoading(false);
    }
  };

  const toggleColumnVisibility = (key: string) => {
    setAvailableColumns(prev => prev.map(col => (col.key === key ? { ...col, visible: !col.visible } : col)));
  };

  const saveColumnConfig = () => {
    const visibleCols = availableColumns.filter(c => c.visible);
    setColumns(visibleCols.map(c => ({ key: c.key, label: c.label })));
    setConfigModalOpen(false);
  };

  const renderCellValue = (value: any) => {
    if (value === null || value === undefined) {
      return <span className="text-muted">—</span>;
    }
    if (typeof value === 'boolean') {
      return value ? '✓' : '✗';
    }
    if (typeof value === 'object') {
      return <code>{JSON.stringify(value)}</code>;
    }
    return String(value);
  };

  return (
    <div className="entity-page">
      <div className="card shadow-sm border-0 entity-card">
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h2 className="mb-0">
                {code} - {resource}
              </h2>
              <p className="text-muted mb-0">
                {data.length} record{data.length !== 1 ? 's' : ''} from all datacenters
              </p>
            </div>
            <Link to={`/icc/instances/${code}`} className="btn btn-secondary">
              <FontAwesomeIcon icon={faArrowLeft} className="me-1" />
              Back to Instances
            </Link>
            <Button color="primary" onClick={() => setConfigModalOpen(true)}>
              <FontAwesomeIcon icon={faCog} className="me-1" />
              Configure Columns
            </Button>
          </div>
        </div>
        <hr className="my-0" />
        {loading ? (
          <div className="text-center py-5">
            <Spinner color="primary" />
            <p className="mt-2 text-muted">Loading data from all datacenters...</p>
          </div>
        ) : (
          <div className="table-responsive">
            <Table className="table-hover mb-0 entity-table">
              <thead>
                <tr>
                  {columns.map(col => (
                    <th key={col.key}>{col.label}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {data.length > 0 ? (
                  data.map((row, idx) => (
                    <tr key={idx}>
                      {columns.map(col => (
                        <td key={col.key}>{renderCellValue(row[col.key])}</td>
                      ))}
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={columns.length || 1} className="text-center text-muted py-4">
                      No data found
                    </td>
                  </tr>
                )}
              </tbody>
            </Table>
          </div>
        )}
      </div>

      <Modal isOpen={configModalOpen} toggle={() => setConfigModalOpen(false)} size="lg">
        <ModalHeader toggle={() => setConfigModalOpen(false)}>Configure Columns</ModalHeader>
        <ModalBody>
          <p className="text-muted">Select which columns to display in the table:</p>
          <Form>
            {availableColumns.map(col => (
              <FormGroup check key={col.key}>
                <Label check>
                  <Input type="checkbox" checked={col.visible} onChange={() => toggleColumnVisibility(col.key)} />
                  {col.label} ({col.key})
                </Label>
              </FormGroup>
            ))}
          </Form>
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={() => setConfigModalOpen(false)}>
            Cancel
          </Button>
          <Button color="primary" onClick={saveColumnConfig}>
            Apply
          </Button>
        </ModalFooter>
      </Modal>
    </div>
  );
};

export default ICCDataView;
