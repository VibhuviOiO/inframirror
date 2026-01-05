import './home.scss';

import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Card, CardBody, Col, Row, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { REDIRECT_URL, getLoginUrl } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const pageLocation = useLocation();
  const navigate = useNavigate();
  const [metrics, setMetrics] = useState({ regions: 0, instances: 0, httpMonitors: 0, agents: 0 });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  });

  useEffect(() => {
    if (account?.login) {
      fetchMetrics();
    }
  }, [account]);

  const fetchMetrics = async () => {
    setLoading(true);
    try {
      const [regions, instances, httpMonitors, agents] = await Promise.all([
        axios.get('/api/regions?size=1'),
        axios.get('/api/instances?size=1'),
        axios.get('/api/http-monitors?size=1'),
        axios.get('/api/agents?size=1'),
      ]);
      setMetrics({
        regions: parseInt(regions.headers['x-total-count'] || '0', 10),
        instances: parseInt(instances.headers['x-total-count'] || '0', 10),
        httpMonitors: parseInt(httpMonitors.headers['x-total-count'] || '0', 10),
        agents: parseInt(agents.headers['x-total-count'] || '0', 10),
      });
    } catch (error) {
      console.error('Error fetching metrics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!account?.login) {
    return (
      <div className="home-dashboard">
        <div className="hero-section">
          <h1>InfraMirror</h1>
          <p className="lead">Infrastructure Truth System for Modern Operations</p>
          <Button color="primary" size="lg" onClick={() => navigate(getLoginUrl(), { state: { from: pageLocation } })}>
            <FontAwesomeIcon icon="sign-in-alt" className="me-2" />
            Sign In to Get Started
          </Button>
        </div>

        <Row className="mt-5 g-4">
          <Col md="4">
            <Card className="feature-card">
              <CardBody>
                <FontAwesomeIcon icon="eye" size="3x" className="text-primary mb-3" />
                <h4>Infrastructure Truth</h4>
                <p className="text-muted">
                  Single authoritative model of what exists, where it runs, how it connects, and what depends on what.
                </p>
              </CardBody>
            </Card>
          </Col>

          <Col md="4">
            <Card className="feature-card">
              <CardBody>
                <FontAwesomeIcon icon="project-diagram" size="3x" className="text-success mb-3" />
                <h4>Dependency-Aware Health</h4>
                <p className="text-muted">Understand how health flows through your infrastructure stack from hardware to URLs.</p>
              </CardBody>
            </Card>
          </Col>

          <Col md="4">
            <Card className="feature-card">
              <CardBody>
                <FontAwesomeIcon icon="robot" size="3x" className="text-info mb-3" />
                <h4>Agent-as-Sensor</h4>
                <p className="text-muted">
                  Lightweight agents observe and report. The control plane owns structure, relationships, and meaning.
                </p>
              </CardBody>
            </Card>
          </Col>
        </Row>

        <Row className="mt-4 g-4">
          <Col md="6">
            <Card className="info-card">
              <CardBody>
                <h5 className="mb-3">What We Monitor</h5>
                <ul className="feature-list">
                  <li>
                    <FontAwesomeIcon icon="check-circle" className="text-success me-2" />
                    Regions & Datacenters
                  </li>
                  <li>
                    <FontAwesomeIcon icon="check-circle" className="text-success me-2" />
                    Bare Metal & Virtual Machines
                  </li>
                  <li>
                    <FontAwesomeIcon icon="check-circle" className="text-success me-2" />
                    HTTP/HTTPS Endpoints
                  </li>
                  <li>
                    <FontAwesomeIcon icon="check-circle" className="text-success me-2" />
                    TCP Services (Databases, Queues)
                  </li>
                  <li>
                    <FontAwesomeIcon icon="check-circle" className="text-success me-2" />
                    Service Dependencies
                  </li>
                </ul>
              </CardBody>
            </Card>
          </Col>

          <Col md="6">
            <Card className="info-card">
              <CardBody>
                <h5 className="mb-3">Built For</h5>
                <ul className="feature-list">
                  <li>
                    <FontAwesomeIcon icon="building" className="text-primary me-2" />
                    Private Datacenters
                  </li>
                  <li>
                    <FontAwesomeIcon icon="cloud" className="text-primary me-2" />
                    Hybrid Cloud Environments
                  </li>
                  <li>
                    <FontAwesomeIcon icon="server" className="text-primary me-2" />
                    Long-Lived Infrastructure
                  </li>
                  <li>
                    <FontAwesomeIcon icon="users" className="text-primary me-2" />
                    Platform & DevOps Teams
                  </li>
                  <li>
                    <FontAwesomeIcon icon="shield-alt" className="text-primary me-2" />
                    Enterprise Operations
                  </li>
                </ul>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }

  return (
    <div className="home-dashboard">
      <div className="dashboard-header mb-4">
        <h2>Infrastructure Monitoring Dashboard</h2>
        <p className="text-muted">Real-time visibility into your infrastructure health</p>
      </div>

      <Row className="g-3">
        <Col md="3">
          <Card className="dashboard-card">
            <CardBody>
              <div className="d-flex align-items-center">
                <FontAwesomeIcon icon="globe" size="2x" className="text-primary me-3" />
                <div>
                  <h6 className="text-muted mb-1">Regions</h6>
                  <h3 className="mb-0">{loading ? '...' : metrics.regions}</h3>
                </div>
              </div>
            </CardBody>
          </Card>
        </Col>

        <Col md="3">
          <Card className="dashboard-card">
            <CardBody>
              <div className="d-flex align-items-center">
                <FontAwesomeIcon icon="server" size="2x" className="text-info me-3" />
                <div>
                  <h6 className="text-muted mb-1">Instances</h6>
                  <h3 className="mb-0">{loading ? '...' : metrics.instances}</h3>
                </div>
              </div>
            </CardBody>
          </Card>
        </Col>

        <Col md="3">
          <Card className="dashboard-card">
            <CardBody>
              <div className="d-flex align-items-center">
                <FontAwesomeIcon icon="chart-line" size="2x" className="text-success me-3" />
                <div>
                  <h6 className="text-muted mb-1">HTTP Monitors</h6>
                  <h3 className="mb-0">{loading ? '...' : metrics.httpMonitors}</h3>
                </div>
              </div>
            </CardBody>
          </Card>
        </Col>

        <Col md="3">
          <Card className="dashboard-card">
            <CardBody>
              <div className="d-flex align-items-center">
                <FontAwesomeIcon icon="robot" size="2x" className="text-warning me-3" />
                <div>
                  <h6 className="text-muted mb-1">Active Agents</h6>
                  <h3 className="mb-0">{loading ? '...' : metrics.agents}</h3>
                </div>
              </div>
            </CardBody>
          </Card>
        </Col>
      </Row>

      <Row className="mt-4 g-3">
        <Col md="6">
          <Card>
            <CardBody>
              <h5 className="mb-3">
                <FontAwesomeIcon icon="link" className="me-2" />
                Quick Links
              </h5>
              <div className="quick-links">
                <Link to="/region" className="quick-link-item">
                  <FontAwesomeIcon icon="globe" className="me-2" />
                  Manage Regions
                </Link>
                <Link to="/datacenter" className="quick-link-item">
                  <FontAwesomeIcon icon="building" className="me-2" />
                  Manage Datacenters
                </Link>
                <Link to="/instance" className="quick-link-item">
                  <FontAwesomeIcon icon="server" className="me-2" />
                  Manage Instances
                </Link>
                <Link to="/http-monitor" className="quick-link-item">
                  <FontAwesomeIcon icon="chart-line" className="me-2" />
                  HTTP Monitors
                </Link>
                <Link to="/monitored-service" className="quick-link-item">
                  <FontAwesomeIcon icon="cogs" className="me-2" />
                  Monitored Services
                </Link>
                <Link to="/agent" className="quick-link-item">
                  <FontAwesomeIcon icon="robot" className="me-2" />
                  Agents
                </Link>
              </div>
            </CardBody>
          </Card>
        </Col>

        <Col md="6">
          <Card>
            <CardBody>
              <h5 className="mb-3">
                <FontAwesomeIcon icon="info-circle" className="me-2" />
                Getting Started
              </h5>
              <div className="getting-started">
                <div className="step-item">
                  <span className="step-number">1</span>
                  <div>
                    <strong>Define Infrastructure</strong>
                    <p className="text-muted mb-0">Create regions, datacenters, and instances</p>
                  </div>
                </div>
                <div className="step-item">
                  <span className="step-number">2</span>
                  <div>
                    <strong>Deploy Agents</strong>
                    <p className="text-muted mb-0">Install monitoring agents on your infrastructure</p>
                  </div>
                </div>
                <div className="step-item">
                  <span className="step-number">3</span>
                  <div>
                    <strong>Configure Monitors</strong>
                    <p className="text-muted mb-0">Set up HTTP and service monitors</p>
                  </div>
                </div>
                <div className="step-item">
                  <span className="step-number">4</span>
                  <div>
                    <strong>Monitor Health</strong>
                    <p className="text-muted mb-0">View real-time status and metrics</p>
                  </div>
                </div>
              </div>
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Home;
