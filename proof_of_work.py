from flask import Flask, render_template
from flask import request
import json
import datetime
import time
import hashlib
import requests
import os
import time

node = Flask(__name__)
NUM_ZEROS = 4
this_nodes_transactions = []

class Block:
  def __init__(self, index, timestamp, data, previous_hash):
    self.index = index
    self.timestamp = timestamp
    self.data = data
    self.previous_hash = previous_hash
    self.hash = self.hash_block()
  
  def hash_block(self):
    sha = hasher.sha256()
    sha.update(str(self.index) + str(self.timestamp) + str(self.data) + str(self.previous_hash))
    return sha.hexdigest()

def mine(txion_json):
  index = 10
  timestamp = datetime.datetime.now()

  data = txion_json
  nonce = 0

  block_hash = calculate_hash(index, data, timestamp, nonce)
  while str(block_hash[0:NUM_ZEROS]) != '0' * NUM_ZEROS:
    nonce += 1
    block_hash = calculate_hash(index, data, timestamp, nonce)
    print(block_hash)
  return nonce, block_hash

def generate_header(index, data, timestamp, nonce):
  return str(index) + str(data) + str(timestamp) + str(nonce)

def calculate_hash(index, data, timestamp, nonce):
  header_string = generate_header(index, data, timestamp, nonce)
  sha = hashlib.sha256()
  sha.update(header_string)
  return sha.hexdigest()

@node.route('/txion', methods=['POST'])
def transaction():
  new_txion = request.get_json()
  this_nodes_transactions.append(new_txion)
  print(new_txion)
  print "New transaction"
  print "FROM: {}".format(new_txion['from'].encode('ascii','replace'))
  print "TO: {}".format(new_txion['to'].encode('ascii','replace'))
  print "AMOUNT: {}\n".format(new_txion['amount'])

  proof_of_work, hashh = mine(new_txion)
  print(proof_of_work)

  time_miner1 = time.time()
  #os.system("curl 'localhost:3400/proof'      -H 'Content-Type: application/json'      -d '{'proof_of_work':" + str(proof_of_work) + ", 'hash':" + str(hashh) + "}'")
  to_send = "curl \"localhost:3400/proof\"      -H \"Content-Type: application/json\"      -d  '{\"proof_of_work\":" + "\"" + str(proof_of_work) + "\", \"hash\":" + "\"" + str(hashh) + "\", \"timestamp\":" + "\"" + str(time_miner1) + "\", \"from\":" + "\"" + str(new_txion["from"]) + "\", \"to\":" + "\"" + str(new_txion["to"]) + "\", \"amount\":" + "\"" + str(new_txion["amount"]) + "\"}'"
  #print(to_send)
  os.system(to_send)
  return 'Transaction Successful'

if __name__ == '__main__':
  node.run(debug=True, host='0.0.0.0', port=4400)