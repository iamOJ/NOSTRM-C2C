from flask import Flask, render_template
from flask import request
import json
import requests
import hashlib as hasher
import datetime
import copy
import time

node = Flask(__name__)
status = False
NUM_ZEROS = 4
time_miner1 = 0
proof_miner1 = 0

class Block:
  def __init__(self, index, timestamp, data, self_hash, previous_hash):
    self.index = index
    self.timestamp = timestamp
    self.data = data
    self.hash = self_hash
    self.previous_hash = previous_hash
    #self.hash = self.hash_block()
  
  def hash_block(self):
    sha = hasher.sha256()
    sha.update(str(self.index) + str(self.timestamp) + str(self.data) + str(self.previous_hash))
    return sha.hexdigest()
"""
@node.route('/mine', methods = ['GET'])
def mine():
  last_block = blockchain[len(blockchain) - 1]
  last_proof = last_block.data['proof-of-work']
  proof = proof_of_work(last_proof)
  this_nodes_transactions.append(
    { "from":"network" , "to":miner_address , "amount":1 }
  )
  new_block_data = {
    "proof-of-work": str(proof),
    "transactions": list(this_nodes_transactions)
  }
  new_block_index = last_block.index + 1
  new_block_timestamp = this_timestamp = date.datetime.now()
  last_block_hash = last_block.hash
  this_nodes_transactions[:] = []
  mined_block = Block(
    new_block_index,
    new_block_timestamp,
    new_block_data,
    last_block_hash
  )
  blockchain.append(mined_block)
  
  block = {"index": new_block_index, "timestamp": str(new_block_timestamp), "data": new_block_data, "hash": last_block_hash}

  return render_template('mine.html', block=block)
"""
def create_genesis_block():
  return Block(0, datetime.datetime.now(), {
    "proof-of-work": 9,
    "transactions": None
  }, 0, 0)

blockchain = []
blockchain.append(create_genesis_block())
this_nodes_transactions = []
peer_nodes = []
mining = True

@node.route('/txion', methods=['POST'])
def transaction():
  global time_miner1, proof
  new_txion = request.get_json()
  print(new_txion)
  print "New transaction"
  print "FROM: {}".format(new_txion['from'].encode('ascii','replace'))
  print "TO: {}".format(new_txion['to'].encode('ascii','replace'))
  print "AMOUNT: {}\n".format(new_txion['amount'])
  #last_proof = last_block.data['proof-of-work']
  proof_miner1 = mine(new_txion)

  time_miner1 = time.time()

  """
  last_block = blockchain[len(blockchain) - 1]
  this_nodes_transactions.append({ "from": "network", "to": miner_address, "amount": 1 })

  new_block_data = {
    "proof-of-work": proof,
    "transactions": list(this_nodes_transactions)
  }
  new_block_index = last_block.index + 1
  new_block_timestamp = this_timestamp = datetime.datetime.now()
  last_block_hash = last_block.hash
  
  mined_block = Block(
    new_block_index,
    new_block_timestamp,
    new_block_data,
    last_block_hash
  )
  blockchain.append(mined_block)
  
  block = {"index": new_block_index, "timestamp": str(new_block_timestamp), "data": new_block_data, "hash": last_block_hash}
  """
  return 'Transaction Successful'
  
@node.route('/proof', methods = ['POST'])
def get_proof():
  global time_miner1, proof_miner1
  new_proof = request.get_json()
  to_send = json.dumps(new_proof)
  #this_nodes_transactions.append(new_txion)
  print(new_proof)
  proof_miner2 = new_proof["proof_of_work"]
  time_miner2 = float(new_proof["timestamp"])

  if(time_miner1 < time_miner2):
    print("\nMINER 1 WINS\n")
    miner_address = "Miner 1"
    block_timestamp = time_miner1
    block_proof_of_work = proof_miner1
  else:
    print("\nMINER 2 WINS\n")
    miner_address = "Miner 2"
    block_timestamp = time_miner2
    block_proof_of_work = proof_miner2
  
  last_block = blockchain[len(blockchain) - 1]
  
  block_from = new_proof["from"]
  block_to = new_proof["to"]
  block_amount = new_proof["amount"]
  block_hash = new_proof["hash"]
  block_index = last_block.index + 1
  block_proof_of_work = new_proof["proof_of_work"]

  this_nodes_transactions.append({"from":block_from, "to":block_to, "amount":block_amount})
  this_nodes_transactions.append({"from":"ledger", "to": miner_address, "amount": 1 })  

  block_data = {"proof-of-work": block_proof_of_work, "transactions": list(this_nodes_transactions)}
  this_nodes_transactions[:] = []
  last_block_hash = last_block.hash

  mined_block = Block(
    block_index,
    block_timestamp,
    block_data,
    block_hash,
    last_block_hash
  )

  blockchain.append(mined_block)

  return to_send

"""
@node.route('/req', methods=['GET'])
def pp():
  status = True
  return render_template('main.html', a=status)
"""

@node.route('/blocks', methods=['GET'])
def get_blocks():
  parent_json = {}
  chain_to_send = copy.copy(blockchain)
  for i in range(0,len(chain_to_send)):
    block = chain_to_send[i]

    block_index = str(block.index)
    block_timestamp = str(block.timestamp)

    if(i==0):
      trans0 = "None"
      trans1 = "None"
      block_hash = "None"
      proof_of_work = "None"
    else:
      block_transaction = block.data["transactions"]
      proof_of_work = block.data["proof-of-work"]
      block_from0 = block_transaction[0]["from"]
      block_to0 = block_transaction[0]["to"]
      block_amount0 = block_transaction[0]["amount"]

      block_from1 = block_transaction[1]["from"]
      block_to1 = block_transaction[1]["to"]
      block_amount1 = block_transaction[1]["amount"]

      trans0 = "FROM=" + block_from0 + ";   TO=" + block_to0 + ";   AMOUNT=" + str(block_amount0)
      trans1 = "FROM=" + block_from1 + ";   TO=" + block_to1 + ";   AMOUNT=" + str(block_amount1)
      
      block_hash = block.hash
      
    chain_to_send[i] = {
      "index": block_index,
      "timestamp": block_timestamp,
      "trans1": trans0,
      "trans2": trans1,
      "hash": block_hash,
      "proof-of-work": proof_of_work
    }

  return render_template('blocks.html', chain=chain_to_send)

@node.route('/jsonn', methods=['GET'])
def get_jsonn_blocks():
  child_list=[]
  chain_to_send = copy.copy(blockchain)

  for i in range(0,len(chain_to_send)):
    block = chain_to_send[i]
    block_json = {
      "index": str(block.index),
      "timestamp": str(block.timestamp),
      "data": block.data,#json.loads(json.dumps(block.data)),
      "prev_hash": block.hash
    }
    child_list.append(block_json)

  parent_json = {
    "blocks" : child_list
  }
  print(type(parent_json))
  to_send = json.dumps(parent_json)
  return to_send

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
  print(nonce)
  return nonce

def generate_header(index, data, timestamp, nonce):
  return str(index) + str(data) + str(timestamp) + str(nonce)

def calculate_hash(index, data, timestamp, nonce):
  header_string = generate_header(index, data, timestamp, nonce)
  sha = hasher.sha256()
  sha.update(header_string)
  return sha.hexdigest()

if __name__ == '__main__':
    node.run(debug=True, host='0.0.0.0', port=3400)