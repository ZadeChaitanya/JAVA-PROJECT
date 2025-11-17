// src/pages/QuizPage.jsx

import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import contentService from '../services/contentService'; // We'll update this service

const QuizPage = () => {
    // Get the Test ID from the URL path (e.g., /student/quiz/5)
    const { testId } = useParams(); 
    const navigate = useNavigate();
    const { user } = useAuth();
    
    const [questions, setQuestions] = useState([]);
    const [answers, setAnswers] = useState({}); // Stores { questionId: submittedAnswer }
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 1. Fetch Questions for the test
    useEffect(() => {
        const fetchQuestions = async () => {
            try {
                // API: GET /api/questions/test/{testId} (Returns list of QuestionDTOs)
                const data = await contentService.getTestQuestions(testId); 
                setQuestions(data);
            } catch (err) {
               console.log(err);
                setError("Failed to load quiz questions.");
            } finally {
                setLoading(false);
            }
        };
        fetchQuestions();
    }, [testId]);

    // 2. Handle answer selection change
    const handleAnswerChange = (questionId, submittedAnswer) => {
        setAnswers(prev => ({ ...prev, [questionId]: submittedAnswer }));
    };

    // 3. Handle final submission
    const handleSubmit = async () => {
        // Validation: Ensure all questions are answered
        if (Object.keys(answers).length !== questions.length) {
            alert('Please answer all questions before submitting.');
            return;
        }

        const submissionData = {
            test: { id: parseInt(testId) },
            score: 0, // Score will be calculated on the backend for integrity
            totalQuestions: questions.length,
            // Construct the crucial nested array for the POST API
            studentAnswers: Object.entries(answers).map(([qId, answer]) => ({
                question: { id: parseInt(qId) },
                submittedAnswer: answer
            }))
        };
        
        try {
            // API: POST /api/results (Backend saves data, links user from token)
            await contentService.submitTestResult(submissionData);
            alert('Assessment Submitted Successfully!');
            // Redirect to a review page or the student dashboard
            navigate('/student/dashboard'); 
        } catch (err) {
          console.log(err);
            setError("Submission failed. Please try again.");
        }
    };

    if (loading) return <div>Loading Assessment...</div>;
    if (error) return <div style={{ color: 'red' }}>Error: {error}</div>;

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: 'auto' }}>
            <h2>Assessment: {questions[0]?.testTitle || 'Loading...'}</h2>
            <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
                {questions.map((q) => (
                    <QuestionComponent 
                        key={q.id} 
                        question={q} 
                        selectedAnswer={answers[q.id]}
                        onAnswerChange={handleAnswerChange}
                    />
                ))}
                <button type="submit" style={{ marginTop: '30px', padding: '15px' }}>Submit Assessment</button>
            </form>
        </div>
    );
};

// --- Helper Component for a Single Question ---
const QuestionComponent = ({ question, selectedAnswer, onAnswerChange }) => (
    <div style={{ marginBottom: '20px', border: '1px solid #ddd', padding: '15px' }}>
        <h4>{question.questionIndex}. {question.questionText}</h4>
        {['A', 'B', 'C', 'D'].map(option => (
            <div key={option}>
                <input
                    type="radio"
                    id={`q${question.id}-${option}`}
                    name={`question-${question.id}`}
                    value={option}
                    checked={selectedAnswer === option}
                    onChange={() => onAnswerChange(question.id, option)}
                    required
                />
                <label htmlFor={`q${question.id}-${option}`}>
                    {option}: {question[`option${option}`]}
                </label>
            </div>
        ))}
    </div>
);

export default QuizPage;