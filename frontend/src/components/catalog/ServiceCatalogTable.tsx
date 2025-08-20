import { useEffect, useState } from "react";

export default function ServiceCatalogTable() {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    fetch("/api/service-catalogs")
      .then((res) => res.json())
      .then((d) => setData(d))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full table-auto">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-4 py-3 text-left text-xs font-bold text-gray-700">Name</th>
            <th className="px-4 py-3 text-left text-xs font-bold text-gray-700">Type</th>
            <th className="px-4 py-3 text-left text-xs font-bold text-gray-700">Port</th>
            <th className="px-4 py-3 text-left text-xs font-bold text-gray-700">Description</th>
            <th className="px-4 py-3 text-left text-xs font-bold text-gray-700">Actions</th>
          </tr>
        </thead>
        <tbody>
          {loading ? (
            <tr><td colSpan={5} className="p-8 text-center">Loading...</td></tr>
          ) : data.length === 0 ? (
            <tr><td colSpan={5} className="p-8 text-center text-gray-400">No services found.</td></tr>
          ) : (
            data.map((row, i) => (
              <tr key={row.id} className={i % 2 === 0 ? "bg-white" : "bg-gray-50"}>
                <td className="px-4 py-3">{row.name}</td>
                <td className="px-4 py-3">{row.type}</td>
                <td className="px-4 py-3">{row.port}</td>
                <td className="px-4 py-3">{row.description}</td>
                <td className="px-4 py-3">{/* actions */}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
