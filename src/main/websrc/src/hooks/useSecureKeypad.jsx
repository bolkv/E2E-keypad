import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

export default function useSecureKeypad() {
    const [keypad, setKeypad] = useState(null);
    const [userInput, setUserInput] = useState([]);
    const [hashes, setHashes] = useState([]);

    const refreshKeypad = useCallback(() => {
        getSecureKeypad();
    }, []);

    useEffect(() => {
        // 6자리 입력이 완료되면 자동으로 백엔드에 전송
        if (userInput.length === 6) {
            const inputString = userInput.join(',');
            alert(`${inputString}`);

            postInput(inputString);
            // 입력 후 키패드를 새로고침
            refreshKeypad();
            // 입력을 초기화
            setUserInput([]);
        }
    }, [userInput]);

    const getSecureKeypad = () => {
        return axios.get('http://localhost:8080/api/keypad')
            .then(response => {
                const { image, hashes } = response.data;
                setKeypad(image);
                setHashes(hashes);
            })
            .catch(error => {
                console.error('Error fetching keypad image:', error);
                throw error;
            });
    };

    const postInput = (input) => {
        axios.post('http://localhost:8080/api/input', { input })
            .then(response => {
                console.log('Input saved successfully:', response.data);
            })
            .catch(error => {
                console.error('Error saving input:', error);
            });
    };

    const onKeyPressed = (index) => {
        const hashValue = hashes[index];
        setUserInput(prevInput => {
            // 길이가 6 이상이면 추가 입력을 하지 않음
            if (prevInput.length >= 6) return prevInput;
            return [...prevInput, hashValue];
        });
    };

    return {
        states: {
            keypad,
            userInput: userInput.join(','), // 문자열로 변환하여 반환
        },
        actions: {
            getSecureKeypad,
            onKeyPressed,
        },
    };
}
