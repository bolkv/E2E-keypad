import { useState, useEffect, useCallback } from'react';
import axios from 'axios';
import JSEncrypt from 'jsencrypt';

export default function useSecureKeypad() {
    const [keypad, setKeypad] = useState(null);
    const [userInput, setUserInput] = useState([]);
    const [hashes, setHashes] = useState([]);
    const [uuid, setUuid] = useState('');
    const [keypadhash, setKeypadhash] = useState('');
    const [timestamp, setTimestamp] = useState('');
    const [publicKey,setPublicKey] = useState('');

    const refreshKeypad = useCallback(() => {
        getSecureKeypad();
    }, []);


    useEffect(() => {
        if (userInput.length === 6) {
            const inputString = userInput.join(',');
            alert(`입력된 값: ${inputString}`);

            const encryptedInputs = userInput.map(input => encryptData(input));

            // 암호화된 값들을 ','로 연결
            const encryptedString = encryptedInputs.join(',');
            postInput({ uuid,timestamp, keypadhash, input: encryptedString });
            setUserInput([]);
            refreshKeypad();

        }
    }, [userInput]);

    const getSecureKeypad = () => {
        return axios.get('http://localhost:8080/api/keypad')
            .then(response => {
                const { image, uuid, timestamp, hashes, keypadhash,publicKey } = response.data;
                setKeypad(image);
                setHashes(hashes);
                setUuid(uuid);
                setTimestamp(timestamp)
                setKeypadhash(keypadhash);
                setPublicKey(publicKey);

             })
            .catch(error => {
                console.error('Error fetching keypad image:', error);
                throw error;
            });
    };

    const postInput = (data) => {
        axios.post('http://localhost:8080/api/input', data)
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
            if (prevInput.length >= 6) return prevInput;
            return [...prevInput, hashValue];
        });
    };

    const encryptData = (data) => {
        if (!publicKey) {
            console.error('Public key is not set');
            return null;
        }
        const encryptor = new JSEncrypt();
        encryptor.setPublicKey(publicKey); // 서버에서 받은 공개 키 사용
        const encryptedData = encryptor.encrypt(data);
        console.log('Fetched public key:', publicKey); // 공개 키를 콘솔에 출력
        if (!encryptedData) {
            console.error('Encryption failed');
        }
        return encryptedData;
    };

    return {
        states: {
            keypad,
            userInput: userInput.join(','),
        },
        actions: {
            getSecureKeypad,
            onKeyPressed,
        },
    };
}
