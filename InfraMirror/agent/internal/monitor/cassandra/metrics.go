package cassandra

import (
	"fmt"
	"time"

	"github.com/gocql/gocql"
)

type CassandraMetrics struct {
	ClusterName      string                 `json:"clusterName"`
	CassandraVersion string                 `json:"cassandraVersion"`
	Datacenter       string                 `json:"datacenter"`
	TotalNodes       int                    `json:"totalNodes"`
	Keyspaces        []KeyspaceInfo         `json:"keyspaces"`
	SystemInfo       map[string]interface{} `json:"systemInfo"`
}

type KeyspaceInfo struct {
	Name              string `json:"name"`
	ReplicationFactor int    `json:"replicationFactor"`
	DurableWrites     bool   `json:"durableWrites"`
}

func CollectMetrics(host string, port int, username, password string) (*CassandraMetrics, error) {
	cluster := gocql.NewCluster(fmt.Sprintf("%s:%d", host, port))
	cluster.Authenticator = gocql.PasswordAuthenticator{Username: username, Password: password}
	cluster.Timeout = 5 * time.Second
	cluster.ConnectTimeout = 5 * time.Second

	session, err := cluster.CreateSession()
	if err != nil {
		return nil, fmt.Errorf("failed to connect: %w", err)
	}
	defer session.Close()

	metrics := &CassandraMetrics{
		SystemInfo: make(map[string]interface{}),
	}

	// Query 1: Cluster info from system.local
	var clusterName, releaseVersion, datacenter string
	var broadcastAddress, listenAddress string
	if err := session.Query(`SELECT cluster_name, release_version, data_center, broadcast_address, listen_address FROM system.local`).
		Scan(&clusterName, &releaseVersion, &datacenter, &broadcastAddress, &listenAddress); err == nil {
		metrics.ClusterName = clusterName
		metrics.CassandraVersion = releaseVersion
		metrics.Datacenter = datacenter
		metrics.SystemInfo["broadcastAddress"] = broadcastAddress
		metrics.SystemInfo["listenAddress"] = listenAddress
	}

	// Query 2: Count peer nodes
	var peerCount int
	if err := session.Query(`SELECT COUNT(*) FROM system.peers`).Scan(&peerCount); err == nil {
		metrics.TotalNodes = peerCount + 1 // +1 for local node
	}

	// Query 3: Keyspace information
	iter := session.Query(`SELECT keyspace_name, replication, durable_writes FROM system_schema.keyspaces`).Iter()
	var keyspaceName string
	var replication map[string]string
	var durableWrites bool
	
	for iter.Scan(&keyspaceName, &replication, &durableWrites) {
		rf := 1
		if rfStr, ok := replication["replication_factor"]; ok {
			fmt.Sscanf(rfStr, "%d", &rf)
		}
		
		metrics.Keyspaces = append(metrics.Keyspaces, KeyspaceInfo{
			Name:              keyspaceName,
			ReplicationFactor: rf,
			DurableWrites:     durableWrites,
		})
	}
	iter.Close()

	// Query 4: Table count per keyspace
	tableCount := make(map[string]int)
	iter = session.Query(`SELECT keyspace_name FROM system_schema.tables`).Iter()
	var ksName string
	for iter.Scan(&ksName) {
		tableCount[ksName]++
	}
	iter.Close()
	metrics.SystemInfo["tableCountByKeyspace"] = tableCount

	return metrics, nil
}
