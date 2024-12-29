from flask import Flask, request, jsonify
import google.generativeai as genai
import requests

app = Flask(__name__)

# Configure Gemini
genai.configure(api_key='AIzaSyD1nZgCABglVYXTslWUvVnyMnoSF6gM5DI')
model = genai.GenerativeModel('gemini-1.5-flash')

@app.route('/analyze', methods=['POST'])
def analyze_code():
    try:
        data = request.json
        file_url = data.get('fileUrl')
        
        if not file_url:
            return jsonify({'error': 'No file URL provided'}), 400

        response = requests.get(file_url)
        if response.status_code != 200:
            return jsonify({'error': 'Failed to download file'}), 400
            
        java_code = response.text
        
        prompt = f"""
        Given this Java code:

        {java_code}

        Create a comprehensive JUnit test class for this code that includes:
        1. All necessary test cases to achieve high code coverage
        2. Edge cases testing
        3. Tests for expected exceptions
        4. Tests for normal inputs
        
        Return only the complete test class code without any explanations.
        """
        
        # Send to Gemini
        response = model.generate_content(prompt)
        
        return jsonify({
            'generated_test': response.text
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0', port=5000)