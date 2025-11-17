import React, { useEffect, useState } from "react";
import api from "../services/api";
import { Toaster, toast } from "react-hot-toast";
import { FaSpinner, FaCheck, FaTimes } from "react-icons/fa";
import clsx from "clsx";

export default function TeacherAttendance() {
  const [standard, setStandard] = useState(10);
  const [date, setDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [students, setStudents] = useState([]);
  const [loadingStudents, setLoadingStudents] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [selectAll, setSelectAll] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchStudents();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [standard]);

  async function fetchStudents() {
    setLoadingStudents(true);
    setError("");
    setStudents([]);

    try {
      const res = await api.get(`/api/teacher/students/class/${standard}`);
      const data = Array.isArray(res.data)
        ? res.data.map((s) => ({
            studentId: s.id ?? s.studentId ?? null,
            name: s.name ?? s.studentName ?? s.email ?? "Unknown",
            present: true,
          }))
        : [];
      setStudents(data);
      setSelectAll(true);
      toast.success(`Loaded ${data.length} students`);
    } catch (err) {
      console.error("Failed to load students:", err);
      setError("Failed to load students — check token or network");
      toast.error("Error fetching students");
    } finally {
      setLoadingStudents(false);
    }
  }

  function toggleStudent(idx) {
    setStudents((prev) => {
      const copy = [...prev];
      copy[idx] = { ...copy[idx], present: !copy[idx].present };
      return copy;
    });
    setSelectAll(false);
  }

  function handleSelectAll() {
    setSelectAll((prev) => {
      const next = !prev;
      setStudents((prevStudents) =>
        prevStudents.map((s) => ({ ...s, present: next }))
      );
      return next;
    });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!students.length) return toast.error("No students loaded!");
    const statuses = students.map((s) => ({
      studentId: s.studentId,
      present: !!s.present,
    }));

    setSubmitting(true);
    const toastId = toast.loading("Submitting attendance...");
    try {
      const body = { date, statuses };
      const res = await api.post("/api/attendance/bulk", body);
      toast.success("Attendance submitted ✓", { id: toastId });
      if (Array.isArray(res.data))
        toast(`${res.data.length} records updated`, { icon: "📋" });
    } catch (err) {
      console.error("Submit error:", err);
      toast.error("Failed to submit attendance", { id: toastId });
    } finally {
      setSubmitting(false);
    }
  }

  const presentCount = students.filter((s) => s.present).length;

  return (
    <div className="min-h-screen bg-gray-950 text-gray-100 p-6">
      <Toaster position="top-right" />
      <div className="max-w-6xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold flex items-center gap-2">
              📘 Bulk Attendance (Teacher)
            </h1>
            <p className="text-sm text-gray-400">
              Manage attendance efficiently with live class view
            </p>
          </div>
          <div className="text-sm bg-gray-800 px-4 py-2 rounded-lg border border-gray-700 shadow">
            <strong>{presentCount}</strong> / {students.length} Present
          </div>
        </div>

        {/* Form Controls */}
        <form
          onSubmit={handleSubmit}
          className="bg-gray-900/70 border border-gray-800 rounded-xl shadow-xl p-6 backdrop-blur space-y-4"
        >
          <div className="grid md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm mb-1 text-gray-400">
                Class / Standard
              </label>
              <select
                value={standard}
                onChange={(e) => setStandard(Number(e.target.value))}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {Array.from({ length: 12 }).map((_, i) => (
                  <option key={i + 1} value={i + 1}>
                    Standard {i + 1}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm mb-1 text-gray-400">Date</label>
              <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="flex items-end gap-3">
              <button
                type="button"
                onClick={fetchStudents}
                disabled={loadingStudents}
                className={clsx(
                  "flex-1 py-2 rounded-lg font-medium transition shadow-md",
                  loadingStudents
                    ? "bg-blue-400/60 cursor-wait"
                    : "bg-blue-600 hover:bg-blue-700"
                )}
              >
                {loadingStudents ? (
                  <FaSpinner className="animate-spin inline mr-2" />
                ) : null}
                {loadingStudents ? "Loading..." : "Reload Students"}
              </button>

              <button
                type="submit"
                disabled={submitting || loadingStudents || !students.length}
                className={clsx(
                  "flex-1 py-2 rounded-lg font-medium transition shadow-md",
                  submitting
                    ? "bg-green-400/60 cursor-wait"
                    : "bg-green-600 hover:bg-green-700"
                )}
              >
                {submitting && <FaSpinner className="animate-spin inline mr-2" />}
                {submitting ? "Submitting..." : "Submit All"}
              </button>
            </div>
          </div>

          {/* Toolbar */}
          <div className="flex flex-wrap justify-between items-center gap-3 pt-2 border-t border-gray-800/70">
            <div className="flex items-center gap-3">
              <label className="inline-flex items-center gap-2 text-sm text-gray-300">
                <input
                  type="checkbox"
                  checked={selectAll}
                  onChange={handleSelectAll}
                  className="w-4 h-4 accent-green-500"
                />
                Select All Present
              </label>
              <button
                type="button"
                onClick={() =>
                  setStudents((prev) =>
                    prev.map((s) => ({ ...s, present: !s.present }))
                  )
                }
                className="text-sm text-blue-400 hover:underline"
              >
                Invert
              </button>
            </div>

            <div className="text-sm text-gray-400">
              Total Students: {students.length}
            </div>
          </div>

          {/* Table Layout */}
          <div className="relative mt-4 max-h-[480px] overflow-y-auto rounded-lg border border-gray-800 bg-gray-900/40 backdrop-blur-sm shadow-inner">
            {loadingStudents ? (
              <div className="text-center py-12 text-gray-400">
                <FaSpinner className="animate-spin inline mr-2" />
                Loading students...
              </div>
            ) : students.length === 0 ? (
              <div className="text-center py-12 text-gray-500 text-sm">
                No students loaded — click “Reload Students”
              </div>
            ) : (
              <table className="w-full text-sm">
                <thead className="sticky top-0 bg-gray-800/90 backdrop-blur-md text-gray-300 uppercase text-xs border-b border-gray-700">
                  <tr>
                    <th className="px-4 py-3 text-left">ID</th>
                    <th className="px-4 py-3 text-left">Name</th>
                    <th className="px-4 py-3 text-center">Status</th>
                  </tr>
                </thead>
                <tbody>
                  {students.map((s, idx) => (
                    <tr
                      key={s.studentId ?? idx}
                      className={clsx(
                        "border-b border-gray-800 hover:bg-gray-800/40 transition-all",
                        idx % 2 === 0 ? "bg-gray-900/40" : "bg-gray-950/30"
                      )}
                    >
                      <td className="px-4 py-2 text-gray-400 font-mono">
                        {s.studentId}
                      </td>
                      <td className="px-4 py-2 font-medium">{s.name}</td>
                      <td className="px-4 py-2 text-center">
                        <button
                          type="button"
                          onClick={() => toggleStudent(idx)}
                          className={clsx(
                            "px-4 py-1 rounded-full font-semibold transition text-sm shadow-sm",
                            s.present
                              ? "bg-green-600/20 text-green-400 hover:bg-green-600/30"
                              : "bg-red-600/20 text-red-400 hover:bg-red-600/30"
                          )}
                        >
                          {s.present ? (
                            <>
                              <FaCheck className="inline mr-1" /> Present
                            </>
                          ) : (
                            <>
                              <FaTimes className="inline mr-1" /> Absent
                            </>
                          )}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
